package com.DucPhuc.Plants_shop.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {
    String otp;
    @Size(min = 8, message = "Password must be between 8 and 32 characters")
    String newPassword;
    String confirmPassword;
}
