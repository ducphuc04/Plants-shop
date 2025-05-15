package com.DucPhuc.Plants_shop.repository;

import com.DucPhuc.Plants_shop.entity.OrderDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetails, String> {
    List<OrderDetails> findByOrder_OrderId(Long OrderId);

    Page<OrderDetails> findByOrder_OrderId(Long OrderId, Pageable pageable);
}
