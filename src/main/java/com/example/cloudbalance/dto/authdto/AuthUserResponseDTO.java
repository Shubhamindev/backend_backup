package com.example.cloudbalance.dto.authdto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthUserResponseDTO {
    private String token;
    private String refreshToken;
}