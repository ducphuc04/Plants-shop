package com.DucPhuc.Plants_shop.repository;

import com.DucPhuc.Plants_shop.entity.EmployeeEdited;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeEditedRepository extends JpaRepository<EmployeeEdited, String> {
}
