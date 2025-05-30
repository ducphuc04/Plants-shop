package com.DucPhuc.Plants_shop.repository;

import com.DucPhuc.Plants_shop.dto.response.DailyRevenueResponse;
import com.DucPhuc.Plants_shop.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
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

    Page<Orders> findByStatusNot(String status, Pageable pageable);
    Page<Orders> findByStatus(String status, Pageable pageable);

    @Query("SELECT SUM(o.totalPrice) FROM Orders o WHERE o.status = 'processed' AND o.orderDate >= :date")
    Integer getTotalIncomeInPeriod(@Param("date") LocalDateTime date);

    @Query("SELECT AVG(o.totalPrice) FROM Orders o WHERE o.status = 'processed' AND o.orderDate >= :date")
    Integer getAverageOrderValueInPeriod(@Param("date") LocalDateTime date);

    @Query(value = """
        SELECT YEAR(o.order_date) AS year,
               MONTH(o.order_date) AS month,
               SUM(o.total_price) AS revenue
        FROM orders o
        WHERE o.order_date >= :startDate
        GROUP BY YEAR(o.order_date), MONTH(o.order_date)
                ORDER BY year, month DESC 
        """, nativeQuery = true)
    List<DailyRevenueResponse> getMonthlyRevenue(@Param("startDate") LocalDateTime startDate);


    @Query(value = "SELECT DATE_FORMAT(o.order_date, '%Y-%m-%d') as date, SUM(o.total_price) as total " +
           "FROM orders o " +
           "WHERE o.order_date >= :startDate AND o.status='processed'"+
           "GROUP BY DATE_FORMAT(o.order_date, '%Y-%m-%d')", nativeQuery = true)
    List<DailyRevenueResponse> getDailyRevenue(LocalDateTime startDate);
}