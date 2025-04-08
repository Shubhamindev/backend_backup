package com.example.cloudbalance.dto.usermanagement;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDTO {
    private Long id;
    private String accountName;
}