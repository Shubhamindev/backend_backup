package com.example.cloudbalance.service.usermanagement;

import com.example.cloudbalance.dto.usermanagement.AccountDTO;
import com.example.cloudbalance.dto.usermanagement.UserManagementRequestDTO;
import com.example.cloudbalance.dto.usermanagement.UserManagementResponseDTO;
import org.springframework.http.ResponseEntity;


import java.util.List;
import java.util.Set;

public interface UserManagementServiceInterface {

    ResponseEntity<List<UserManagementResponseDTO>> getAllUsers();
    ResponseEntity<UserManagementResponseDTO> getUserById(Long id);
    ResponseEntity<String> createUser(UserManagementRequestDTO userRequest);
    ResponseEntity<String> editUser(Long id, UserManagementRequestDTO userRequest);
    ResponseEntity<String> deleteUser(Long id);
    ResponseEntity<List<AccountDTO>> getOrphanedAccounts();
    ResponseEntity<List<AccountDTO>> getAssignedAccounts();
    ResponseEntity<String> assignAccounts(Long userId, Set<Long> accountIds);
    ResponseEntity<String> unassignAccounts(Long userId, Set<Long> accountIds);
    ResponseEntity<String> changeUserRole(Long userId, String newRole);
}
