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
public class EmployeeEdited {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int employeeEditedId;
    private int employeeId;
    private int productId;
    private Date edit;
}
