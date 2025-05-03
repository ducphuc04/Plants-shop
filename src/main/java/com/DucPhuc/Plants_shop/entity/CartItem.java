package com.DucPhuc.Plants_shop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long cartItemId;

    private int quantity;
    @ManyToOne
    @JoinColumn(name="productId")
    Product product;


    @ManyToOne
    @JoinColumn(name="cartId")
    Cart cart;
}
