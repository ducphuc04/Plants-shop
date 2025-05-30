package com.DucPhuc.Plants_shop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class OrderResponse {
    Long orderId;
    Date orderDate;
    Date paymentDate;
    int totalProduct;
    int totalPrice;
    String address;
    String phone;
    String name;
    String email;
    String paymentMethod;
    String status;
}
