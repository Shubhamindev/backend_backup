package com.example.cloudbalance.service.usermanagement;

import com.example.cloudbalance.dto.usermanagement.AccountDTO;
import com.example.cloudbalance.dto.usermanagement.UserManagementRequestDTO;
import com.example.cloudbalance.dto.usermanagement.UserManagementResponseDTO;
import com.example.cloudbalance.entity.auth.AccountEntity;
import com.example.cloudbalance.entity.auth.RoleEntity;
import com.example.cloudbalance.entity.auth.UsersEntity;
import com.example.cloudbalance.globalexceptionhandler.CustomException;
import com.example.cloudbalance.mapper.usermanagement.UserMapper;
import com.example.cloudbalance.repository.AccountRepository;
import com.example.cloudbalance.repository.RoleRepository;
import com.example.cloudbalance.repository.UserRepository;
import com.example.cloudbalance.util.DtoToEntityMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserManagementService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final DtoToEntityMapper dtoToEntityMapper;
    private final UserMapper userMapper;

    public UserManagementService(UserRepository userRepository, RoleRepository roleRepository,
                                 AccountRepository accountRepository, PasswordEncoder passwordEncoder,
                                 DtoToEntityMapper dtoToEntityMapper, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.dtoToEntityMapper = dtoToEntityMapper;
        this.userMapper = userMapper;
    }

    public ResponseEntity<List<UserManagementResponseDTO>> getAllUsers() {
        List<UsersEntity> users = userRepository.findAll();
        List<UserManagementResponseDTO> userDTOs = users.stream()
                .map(userMapper::toUserManagementResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    public ResponseEntity<UserManagementResponseDTO> getUserById(Long id) {
        UsersEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        return ResponseEntity.ok(userMapper.toUserManagementResponseDTO(user));
    }

    public ResponseEntity<String> createUser(UserManagementRequestDTO userRequest) {
        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new CustomException("Email already exists!", HttpStatus.BAD_REQUEST);
        }
        if (userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
            throw new CustomException("Username already exists!", HttpStatus.BAD_REQUEST);
        }
        RoleEntity role = roleRepository.findByName(userRequest.getRole())
                .orElseThrow(() -> new CustomException("Role not found!", HttpStatus.NOT_FOUND));
        UsersEntity newUser = UsersEntity.builder()
                .email(userRequest.getEmail())
                .username(userRequest.getUsername())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .role(role)
                .build();

        userRepository.save(newUser);
        return ResponseEntity.ok("User created successfully!");
    }


    public ResponseEntity<String> editUser(Long id, UserManagementRequestDTO userRequest) {
        UsersEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if (!user.getEmail().equals(userRequest.getEmail()) &&
                userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists!");
        }

        if (!user.getUsername().equals(userRequest.getUsername()) &&
                userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists!");
        }

        user.setEmail(userRequest.getEmail());
        user.setUsername(userRequest.getUsername());

        if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }

        if (!user.getRole().getName().equals(userRequest.getRole())) {
            RoleEntity newRole = roleRepository.findByName(userRequest.getRole())
                    .orElseThrow(() -> new RuntimeException("Role not found!"));
            user.setRole(newRole);
        }

        userRepository.save(user);
        return ResponseEntity.ok("User updated successfully!");
    }

    public ResponseEntity<String> deleteUser(Long id) {
        UsersEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        user.getAccounts().clear();
        userRepository.save(user);
        userRepository.delete(user);

        return ResponseEntity.ok("User deleted successfully!");
    }

    public ResponseEntity<List<AccountDTO>> getOrphanedAccounts() {
        List<AccountEntity> orphanedAccounts = accountRepository.findOrphanedAccounts();
        List<AccountDTO> accountDTOs = orphanedAccounts.stream()
                .map(account -> new AccountDTO(account.getId(), account.getAccountName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(accountDTOs);
    }

    public ResponseEntity<List<AccountDTO>> getAssignedAccounts() {
        List<AccountEntity> assignedAccounts = accountRepository.findAssignedAccounts();
        List<AccountDTO> accountDTOs = assignedAccounts.stream()
                .map(account -> new AccountDTO(account.getId(), account.getAccountName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(accountDTOs);
    }

    public ResponseEntity<String> assignAccounts(Long userId, Set<Long> accountIds) {
        UsersEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if (!user.getRole().getName().equals("USER")) {
            return ResponseEntity.badRequest().body("Accounts can only be assigned to USER role users!");
        }

        Set<AccountEntity> accounts = accountRepository.findAllByIdIn(accountIds);
        user.getAccounts().addAll(accounts);
        userRepository.save(user);
        return ResponseEntity.ok("Accounts assigned successfully!");
    }

    public ResponseEntity<String> unassignAccounts(Long userId, Set<Long> accountIds) {
        UsersEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        Set<AccountEntity> accountsToRemove = user.getAccounts().stream()
                .filter(account -> accountIds.contains(account.getId()))
                .collect(Collectors.toSet());

        user.getAccounts().removeAll(accountsToRemove);
        userRepository.save(user);
        return ResponseEntity.ok("Accounts unassigned successfully!");
    }

    public ResponseEntity<String> changeUserRole(Long userId, String newRole) {
        UsersEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        RoleEntity role = roleRepository.findByName(newRole)
                .orElseThrow(() -> new RuntimeException("Role not found!"));

        user.setRole(role);
        userRepository.save(user);
        return ResponseEntity.ok("User role changed successfully!");
    }
}