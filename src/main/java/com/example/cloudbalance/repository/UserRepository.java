package com.example.cloudbalance.repository;

import com.example.cloudbalance.entity.auth.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UsersEntity, Long> {
    Optional<UsersEntity> findByEmail(String email);
    Optional<UsersEntity> findByUsername(String username);
}