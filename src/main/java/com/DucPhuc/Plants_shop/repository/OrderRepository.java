package com.DucPhuc.Plants_shop.repository;

import com.DucPhuc.Plants_shop.dto.response.UserSummaryResponse;
import com.DucPhuc.Plants_shop.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Orders, String> {

    Page<Orders> findByUser_username(String username, Pageable pageable);
    Optional<Orders> findByOrderId(Long orderId);

    @Query(
            value = """
            SELECT u.username, u.name, u.phone,
                   COALESCE(SUM(o.total_product), 0) AS totalProduct,
                   COALESCE(SUM(o.total_price), 0) AS totalPrice
            FROM user u
            JOIN orders o ON u.id = o.user_id
            GROUP BY u.id, u.username, u.name, u.phone
            """,
            countQuery = """
            SELECT COUNT(DISTINCT o.user_id)
            FROM orders o
            """,
            nativeQuery = true
    )
    Page<Object[]> findAllUserSummaries(Pageable pageable);
}
