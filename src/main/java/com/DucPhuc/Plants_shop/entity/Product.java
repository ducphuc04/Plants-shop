package com.DucPhuc.Plants_shop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long productId;
    private String productName;
    private int price;
    private String description;
    private String image;
    private int stock;
    private String category;
    private String createdAt;
}
