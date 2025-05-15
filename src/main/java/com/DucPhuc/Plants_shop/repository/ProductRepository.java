package com.DucPhuc.Plants_shop.repository;

import com.DucPhuc.Plants_shop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductName(String productName);
    Optional<Product> findByProductId(long productId);
    boolean existsByProductName(String productName);
    @Query("SELECT DISTINCT p.category FROM Product p")
    List<String> findAllCategories();
    Page<Product> findByCategory(String category, Pageable pageable);
}
