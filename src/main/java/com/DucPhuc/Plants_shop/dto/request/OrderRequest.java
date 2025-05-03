package com.DucPhuc.Plants_shop.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class OrderRequest {
    String paymentMethod;
    String name;
    String phone;
    String address;
    String email;
}
