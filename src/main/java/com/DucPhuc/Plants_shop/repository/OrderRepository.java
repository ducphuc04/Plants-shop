package com.DucPhuc.Plants_shop.repository;

import com.DucPhuc.Plants_shop.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Orders, String> {

    Page<Orders> findByUser_username(String username, Pageable pageable);
    Optional<Orders> findByOrderId(Long orderId);
}
