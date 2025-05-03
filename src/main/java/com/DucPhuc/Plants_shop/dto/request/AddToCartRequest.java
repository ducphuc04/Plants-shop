package com.DucPhuc.Plants_shop.dto.request;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AddToCartRequest {
    private Long productId;
    private int quantity;
}
