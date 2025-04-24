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
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId;
    private int userId;
    private Date orderDate;
    private String status;
    private int employeeId;
    private int shipping;
    private String address;
    private String phone;
    private String email;
    private String paymentMethod;

}
