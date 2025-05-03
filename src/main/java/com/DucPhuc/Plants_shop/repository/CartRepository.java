package com.DucPhuc.Plants_shop.repository;

import com.DucPhuc.Plants_shop.entity.Cart;
import com.DucPhuc.Plants_shop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);

}
