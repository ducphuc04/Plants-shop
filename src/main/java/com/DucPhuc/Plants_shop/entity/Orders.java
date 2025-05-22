package com.DucPhuc.Plants_shop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long orderId;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private Date orderDate;
    private String status;
    @ManyToOne
    @JoinColumn(name = "employeeId", nullable = true)
    private Employee employee;
    private int totalProduct;
    private int totalPrice;
    private String address;
    private String phone;
    private String email;
    private String paymentMethod;
    private Date paymentDate;
    @OneToMany(mappedBy = "order")
    private List<OrderDetails> orderDetails;
}
