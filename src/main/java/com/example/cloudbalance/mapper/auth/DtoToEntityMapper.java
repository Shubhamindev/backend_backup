package com.example.cloudbalance.util;

import com.example.cloudbalance.dto.authdto.UserRequestDTO;
import com.example.cloudbalance.entity.auth.RoleEntity;
import com.example.cloudbalance.entity.auth.UsersEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DtoToEntityMapper {

    public UsersEntity toUserEntity(UserRequestDTO userRequest, RoleEntity userRole, PasswordEncoder passwordEncoder) {
        return UsersEntity.builder()
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .role(userRole)
                .build();
    }
}