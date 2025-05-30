package com.DucPhuc.Plants_shop.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class EmployeeUpdateRequest {
    String address;
    String phone;
    String email;
    String oldPassword;
    String newPassword;
    String confirmPassword;

}
