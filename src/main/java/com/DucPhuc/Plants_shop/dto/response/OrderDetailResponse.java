package com.DucPhuc.Plants_shop.dto.response;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class OrderDetailResponse {
    String productName;
    Integer quantity;
    Integer price;
    Integer totalPrice;
}
