package com.DucPhuc.Plants_shop.dto.request;

import jakarta.validation.constraints.Email;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ForgotPasswordRequest {
    @Email(message = "Email is not valid")
    private String email;
    private String username;
}
