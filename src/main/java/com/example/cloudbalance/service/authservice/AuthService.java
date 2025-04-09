package com.example.cloudbalance.service.authservice;

import com.example.cloudbalance.dto.authdto.AuthUserRequestDTO;
import com.example.cloudbalance.dto.authdto.AuthUserResponseDTO;
import com.example.cloudbalance.entity.auth.RoleEntity;
import com.example.cloudbalance.entity.auth.SessionEntity;
import com.example.cloudbalance.entity.auth.UsersEntity;
import com.example.cloudbalance.repository.RoleRepository;
import com.example.cloudbalance.repository.SessionRepository;
import com.example.cloudbalance.repository.UserRepository;
import com.example.cloudbalance.util.DtoToEntityMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class AuthService implements IAuthService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final RoleRepository roleRepository;
    private final com.example.cloudbalance.service.authservice.JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final DtoToEntityMapper dtoToEntityMapper;

    public AuthService(UserRepository userRepository, SessionRepository sessionRepository, RoleRepository roleRepository,
                       com.example.cloudbalance.service.authservice.JwtService jwtService, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder,
                       DtoToEntityMapper dtoToEntityMapper) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.roleRepository = roleRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.dtoToEntityMapper = dtoToEntityMapper;
    }

    public ResponseEntity<AuthUserResponseDTO> loginUser(AuthUserRequestDTO userRequest) {
        try {
            Authentication authentication;
            Optional<UsersEntity> userOptional = userRepository.findByEmail(userRequest.getEmail());
            if (userOptional.isPresent()) {
                authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(userRequest.getEmail(), userRequest.getPassword()));
            } else {
                authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(userRequest.getUsername(), userRequest.getPassword()));
            }

            String token = jwtService.generateToken(userRequest.getEmail());
            String refreshToken = jwtService.generateRefreshToken(userRequest.getEmail());

            UsersEntity user = userRepository.findByEmail(userRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            SessionEntity newSession = SessionEntity.builder()
                    .tokenId(token)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .isValid(true)
                    .user(user)
                    .build();
            sessionRepository.save(newSession);

            return ResponseEntity.ok(new AuthUserResponseDTO(token, refreshToken));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(null);
        }
    }

    public ResponseEntity<String> registerUser(AuthUserRequestDTO userRequest) {
        if (userRepository.findByEmail(userRequest.getEmail()).isPresent() || userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Email or Username already exists!");
        }
        RoleEntity userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Role not found!"));
        UsersEntity newUser = createUserEntity(userRequest, userRole);
        userRepository.save(newUser);
        return ResponseEntity.ok("User registered successfully!");
    }

    public ResponseEntity<String> logoutUser(String token) {
        sessionRepository.findByTokenId(token).ifPresent(session -> {
            session.setIsValid(false);
            session.setUpdatedAt(LocalDateTime.now());
            sessionRepository.save(session);
        });
        return ResponseEntity.ok("Logged out successfully");
    }

    public UsersEntity createUserEntity(AuthUserRequestDTO userRequest, RoleEntity userRole) {
        return dtoToEntityMapper.toUserEntity(userRequest, userRole, passwordEncoder);
    }

    public boolean userExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public ResponseEntity<AuthUserResponseDTO> refreshToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        String newToken = jwtService.generateToken(username);
        String newRefreshToken = jwtService.generateRefreshToken(username);
        return ResponseEntity.ok(new AuthUserResponseDTO(newToken, newRefreshToken));
    }

    public ResponseEntity<String > updatePassword(AuthUserRequestDTO userRequest){
        UsersEntity user = userRepository.findByEmail(userRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            userRepository.save(user);
            return ResponseEntity.ok("Password updated successfully!");
        } else {
            return ResponseEntity.badRequest().body("Invalid password!");
        }
    }


    public ResponseEntity<String> registerAdmin(AuthUserRequestDTO userRequest) {
        if (userRepository.findByEmail(userRequest.getEmail()).isPresent() || userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Email or Username already exists!");
        }
        RoleEntity adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("Role not found!"));
        UsersEntity newAdmin = createUserEntity(userRequest, adminRole);
        userRepository.save(newAdmin);
        return ResponseEntity.ok("Admin registered successfully!");
    }
}