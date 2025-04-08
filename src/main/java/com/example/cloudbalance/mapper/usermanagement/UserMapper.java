package com.example.cloudbalance.mapper.usermanagement;

import com.example.cloudbalance.dto.usermanagement.AccountDTO;
import com.example.cloudbalance.dto.usermanagement.UserManagementResponseDTO;
import com.example.cloudbalance.entity.auth.UsersEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {
    public UserManagementResponseDTO toUserManagementResponseDTO(UsersEntity user) {
        return UserManagementResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole().getName())
                .accounts(user.getAccounts().stream()
                        .map(account -> AccountDTO.builder()
                                .id(account.getId())
                                .accountName(account.getAccountName())
                                .build())
                        .collect(Collectors.toSet()))
                .build();
    }
}