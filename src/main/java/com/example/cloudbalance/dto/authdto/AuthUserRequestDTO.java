package com.example.cloudbalance.dto.authdto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthUserRequestDTO {
    private String email;
    private String username;
    private String password;
}