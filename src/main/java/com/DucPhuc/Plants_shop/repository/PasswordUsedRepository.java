package com.DucPhuc.Plants_shop.repository;

import com.DucPhuc.Plants_shop.entity.PasswordUsed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PasswordUsedRepository extends JpaRepository<PasswordUsed, Long> {
    boolean existsByPasswordAndUser_Username(String password, String username);
    List<PasswordUsed> findByUser_Username(String username);
}
