package com.DucPhuc.Plants_shop.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Getter
public class UserSummaryResponse {
    String name;
    String phone;
    String username;
    int totalProduct;
    int totalPrice;
    public UserSummaryResponse(String username, String name, String phone, int totalProductSum, int totalPriceSum) {
        this.name = name;
        this.username = username;
        this.phone = phone;
        this.totalProduct = totalProductSum;
        this.totalPrice = totalPriceSum;
    }
}
