package com.DucPhuc.Plants_shop.repository;

import com.DucPhuc.Plants_shop.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {
}
