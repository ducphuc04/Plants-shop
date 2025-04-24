package com.DucPhuc.Plants_shop.repository;

import com.DucPhuc.Plants_shop.entity.OtpResetToken;
import com.DucPhuc.Plants_shop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpResetTokenRepository extends JpaRepository<OtpResetToken, Long> {
    Optional<OtpResetToken> findByUserId(String userId);
    Optional<OtpResetToken> findByOtp(String otp);
    void deleteByOtp(String otp);
    boolean existsByOtp(String otp);

}
