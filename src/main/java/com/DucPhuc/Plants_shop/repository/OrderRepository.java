package com.DucPhuc.Plants_shop.repository;

import com.DucPhuc.Plants_shop.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Orders, String> {
}
