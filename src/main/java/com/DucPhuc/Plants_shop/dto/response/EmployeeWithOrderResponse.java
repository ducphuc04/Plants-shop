package com.DucPhuc.Plants_shop.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class EmployeeWithOrderResponse {
    Long employeeId;
    String fullName;
    String role;
    String address;
    String phone;
    int totalOrder;
}
