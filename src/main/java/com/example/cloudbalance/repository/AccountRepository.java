package com.example.cloudbalance.repository;

import com.example.cloudbalance.entity.auth.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
    Set<AccountEntity> findAllByIdIn(Set<Long> ids);
    @Query("SELECT a FROM AccountEntity a WHERE a.users IS EMPTY")
    List<AccountEntity> findOrphanedAccounts();
    @Query("SELECT a FROM AccountEntity a WHERE a.users IS NOT EMPTY")
    List<AccountEntity> findAssignedAccounts();
}