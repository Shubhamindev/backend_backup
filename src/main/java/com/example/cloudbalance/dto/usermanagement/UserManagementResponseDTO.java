package com.example.cloudbalance.dto.usermanagement;

import lombok.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserManagementResponseDTO {
    private Long id;
    private String email;
    private String username;
    private String role;
    private Set<AccountDTO> accounts;
}