package com.DucPhuc.Plants_shop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    @Column(unique = true)
    @Email
    private String email;
    @Column(unique = true)
    private String username;
    private String password;
    private String address;
    private LocalDateTime dateOfCreation;
    private String phone;
}
