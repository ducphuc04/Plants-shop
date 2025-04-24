package com.DucPhuc.Plants_shop.dto.request;


import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class UserCreationRequest {
    @Size(min = 3, message = "Username must be at least 3 character")
    private String username;

    @Size(min = 8, message = "Password must be between 8 and 32 characters")
    private String password;

    private String confirmPassword;
}
