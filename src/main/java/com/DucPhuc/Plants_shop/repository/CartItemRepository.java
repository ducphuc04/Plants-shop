package com.DucPhuc.Plants_shop.repository;

import com.DucPhuc.Plants_shop.entity.Cart;
import com.DucPhuc.Plants_shop.entity.CartItem;
import com.DucPhuc.Plants_shop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProduct_ProductId(Cart cart, Long productId);
}
