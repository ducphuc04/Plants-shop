package com.DucPhuc.Plants_shop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long employeeId;
    @Column(unique = true)
    private String username;
    private String password;
    private String fullName;
    private String role;
    private String address;
    @Column(unique = true)
    private String phone;
    @Column(unique = true)
    private String email;
    private Date createBy;
}
