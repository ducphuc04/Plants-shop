package com.DucPhuc.Plants_shop.dto.request;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateUserRequest {
    private String name;
    private String email;
    private String phone;
    private String address;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
