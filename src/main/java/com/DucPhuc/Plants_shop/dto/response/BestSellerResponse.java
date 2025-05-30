package com.DucPhuc.Plants_shop.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class BestSellerResponse {
    Long productId;
    String productName;
    int totalSales;
    int totalPrice;
    int currentStock;
}
