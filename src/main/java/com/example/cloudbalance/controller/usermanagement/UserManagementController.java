package com.example.cloudbalance.controller.usermanagement;

import com.example.cloudbalance.dto.usermanagement.AccountDTO;
import com.example.cloudbalance.dto.usermanagement.UserManagementRequestDTO;
import com.example.cloudbalance.dto.usermanagement.UserManagementResponseDTO;
import com.example.cloudbalance.service.usermanagement.UserManagementServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/usermanagement")
@CrossOrigin(origins = "http://localhost:5173")
public class UserManagementController {
    private final UserManagementServiceInterface userManagementService;
    public UserManagementController(UserManagementServiceInterface userManagementService) {
        this.userManagementService = userManagementService;
    }
    @GetMapping("/users")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'READONLY')")
    public ResponseEntity<List<UserManagementResponseDTO>> getAllUsers() {
        return userManagementService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'READONLY')")
    public ResponseEntity<UserManagementResponseDTO> getUserById(@PathVariable Long id) {
        return userManagementService.getUserById(id);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> createUser(@Valid @RequestBody UserManagementRequestDTO userRequest) {
        return userManagementService.createUser(userRequest);
    }

    @PutMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> editUser(
            @PathVariable Long id,
            @RequestBody UserManagementRequestDTO userRequest) {
        return userManagementService.editUser(id, userRequest);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        return userManagementService.deleteUser(id);
    }

    @GetMapping("/accounts/orphaned")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'READONLY')")
    public ResponseEntity<List<AccountDTO>> getOrphanedAccounts() {
        return userManagementService.getOrphanedAccounts();
    }

    @GetMapping("/accounts/assigned")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'READONLY')")
    public ResponseEntity<List<AccountDTO>> getAssignedAccounts() {
        return userManagementService.getAssignedAccounts();
    }

    @PutMapping("/assign-accounts/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> assignAccounts(
            @PathVariable Long userId,
            @RequestBody Set<Long> accountIds) {
        return userManagementService.assignAccounts(userId, accountIds);
    }

    @PutMapping("/unassign-accounts/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> unassignAccounts(
            @PathVariable Long userId,
            @RequestBody Set<Long> accountIds) {
        return userManagementService.unassignAccounts(userId, accountIds);
    }

    @PutMapping("/change-role/{userId}/{newRole}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> changeUserRole(
            @PathVariable Long userId,
            @PathVariable String newRole) {
        return userManagementService.changeUserRole(userId, newRole);
    }
}
