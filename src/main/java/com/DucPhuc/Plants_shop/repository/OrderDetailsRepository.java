package com.DucPhuc.Plants_shop.repository;

import com.DucPhuc.Plants_shop.dto.response.BestSellerResponse;
import com.DucPhuc.Plants_shop.entity.OrderDetails;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetails, String> {
    List<OrderDetails> findByOrder_OrderId(Long OrderId);

    Page<OrderDetails> findByOrder_OrderId(Long OrderId, Pageable pageable);

    @Query("SELECT new com.DucPhuc.Plants_shop.dto.response.BestSellerResponse(" +
            "p.productId, p.productName, CAST(SUM(od.quantity) AS int), CAST(SUM(od.quantity * p.price) AS int), p.stock) " +
            "FROM OrderDetails od " +
            "JOIN od.product p " +
            "JOIN od.order o " +
            "WHERE o.status = 'processed' " +
            "GROUP BY p.productId, p.productName, p.stock " +
            "ORDER BY SUM(od.quantity) DESC")
    List<BestSellerResponse> findBestSellingProducts(@Param("limit") int limit);

    void deleteAllByProduct_ProductId(Long productId);

}
