package com.example.cloudbalance.repository;

import com.example.cloudbalance.entity.auth.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<SessionEntity, Long> {
    Optional<SessionEntity> findByTokenId(String tokenId);
}