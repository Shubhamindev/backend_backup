package com.example.cloudbalance.controller.usermanagement;

import com.example.cloudbalance.dto.usermanagement.AccountDTO;
import com.example.cloudbalance.dto.usermanagement.UserManagementRequestDTO;
import com.example.cloudbalance.dto.usermanagement.UserManagementResponseDTO;
import com.example.cloudbalance.service.usermanagement.UserManagementService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/usermanagement")
@CrossOrigin(origins = "http://localhost:5173")
public class UserManagementController {
    private final UserManagementService userManagementService;

    public UserManagementController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }
    @GetMapping("/users")
    public ResponseEntity<List<UserManagementResponseDTO>> getAllUsers() {
        return userManagementService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserManagementResponseDTO> getUserById(@PathVariable Long id) {
        return userManagementService.getUserById(id);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createUser(@Valid @RequestBody UserManagementRequestDTO userRequest) {
        return userManagementService.createUser(userRequest);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<String> editUser(
            @PathVariable Long id,
            @RequestBody UserManagementRequestDTO userRequest) {
        return userManagementService.editUser(id, userRequest);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        return userManagementService.deleteUser(id);
    }

    @GetMapping("/accounts/orphaned")
    public ResponseEntity<List<AccountDTO>> getOrphanedAccounts() {
        return userManagementService.getOrphanedAccounts();
    }

    @GetMapping("/accounts/assigned")
    public ResponseEntity<List<AccountDTO>> getAssignedAccounts() {
        return userManagementService.getAssignedAccounts();
    }

    @PutMapping("/assign-accounts")
    public ResponseEntity<String> assignAccounts(
            @RequestParam Long userId,
            @RequestParam Set<Long> accountIds) {
        return userManagementService.assignAccounts(userId, accountIds);
    }

    @PutMapping("/unassign-accounts")
    public ResponseEntity<String> unassignAccounts(
            @RequestParam Long userId,
            @RequestParam Set<Long> accountIds) {
        return userManagementService.unassignAccounts(userId, accountIds);
    }

    @PutMapping("/change-role/{userId}")
    public ResponseEntity<String> changeUserRole(
            @PathVariable Long userId,
            @RequestParam String newRole) {
        return userManagementService.changeUserRole(userId, newRole);
    }
}