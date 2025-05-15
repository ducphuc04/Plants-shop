package com.DucPhuc.Plants_shop.repository;

import com.DucPhuc.Plants_shop.entity.Employee;
import com.DucPhuc.Plants_shop.entity.EmployeeEdited;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
    Optional<Employee> findByUsername(String username);
    boolean existsByUsername(String username);
    @Query("""
            SELECT e.employeeId, e.fullName, e.role, e.phone, e.address, COUNT(o.id)
            FROM Employee e LEFT JOIN Orders o ON o.employee = e
            GROUP BY e.employeeId, e.fullName, e.role, e.phone, e.address
            """)
    Page<Object[]> findAllWithOrderCount(Pageable pageable);
}
