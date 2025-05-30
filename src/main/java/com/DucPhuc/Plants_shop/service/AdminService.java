package com.DucPhuc.Plants_shop.service;

import com.DucPhuc.Plants_shop.dto.request.CreateEmployeeRequest;
import com.DucPhuc.Plants_shop.dto.request.EmployeeUpdateRequest;
import com.DucPhuc.Plants_shop.dto.response.*;
import com.DucPhuc.Plants_shop.entity.Employee;
import com.DucPhuc.Plants_shop.entity.Orders;
import com.DucPhuc.Plants_shop.entity.User;
import com.DucPhuc.Plants_shop.exception.AppException;
import com.DucPhuc.Plants_shop.exception.ErrorCode;
import com.DucPhuc.Plants_shop.repository.EmployeeRepository;
import com.DucPhuc.Plants_shop.repository.OrderRepository;
import com.DucPhuc.Plants_shop.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@EnableMethodSecurity
public class AdminService {

    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @PreAuthorize("hasRole('ADMIN')")
    public PagingResponse<UserSummaryResponse> getAllUsers(Pageable pageable) {
        Page<Object[]> results = orderRepository.findAllUserSummaries(pageable);

        List<UserSummaryResponse> summaries = results.getContent().stream()
                .map(row -> new UserSummaryResponse(
                        (String) row[0],
                        (String) row[1],
                        (String) row[2],
                        ((Number) row[3]).intValue(),
                        ((Number) row[4]).intValue()
                ))
                .collect(Collectors.toList());

        return PagingResponse.of(
                summaries,
                results.getNumber(),
                results.getTotalPages(),
                results.getTotalElements()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    public EmployeeResponse createEmployee(CreateEmployeeRequest request)
    {
        if (employeeRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.EMPLOYEE_EXISTED);

        Employee employee = new Employee();
        employee.setUsername(request.getUsername());
        employee.setPassword(passwordEncoder.encode(request.getPassword()));
        employee.setFullName(request.getFullName());
        employee.setRole(request.getRole());
        employee.setPhone(request.getPhone());
        employee.setAddress(request.getAddress());
        employee.setEmail(request.getEmail());
        employee.setCreateBy(new java.util.Date());
        employee = employeeRepository.save(employee);

        return EmployeeResponse.builder()
                .employeeId(employee.getEmployeeId())
                .fullName(employee.getFullName())
                .phone(employee.getPhone())
                .address(employee.getAddress())
                .role(employee.getRole())
                .build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public EmployeeResponse getEmployee(){

        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        Employee employee = employeeRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));

        return EmployeeResponse.builder()
                .employeeId(employee.getEmployeeId())
                .username(employee.getUsername())
                .fullName(employee.getFullName())
                .role(employee.getRole())
                .address(employee.getAddress())
                .phone(employee.getPhone())
                .build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public EmployeeResponse updateEmployee(String username, EmployeeUpdateRequest request){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        if (!name.equals(username)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        Employee employee = employeeRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));
        if (request.getPhone() != null)
            employee.setPhone(request.getPhone());
        if (request.getAddress() != null)
            employee.setAddress(request.getAddress());
        if (request.getEmail() != null)
            employee.setEmail(request.getEmail());

        if (request.getOldPassword() != null) {
            boolean isSame = passwordEncoder.matches(request.getOldPassword(), employee.getPassword());
            if (request.getNewPassword().equals(request.getConfirmPassword())) {
                if (isSame) {
                    employee.setPassword(passwordEncoder.encode(request.getNewPassword()));
                } else {
                    throw new AppException(ErrorCode.OLD_PASSWORD_IS_WRONG);
                }
            } else {
                throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
            }
        }

        employeeRepository.save(employee);

        return EmployeeResponse.builder()
                .employeeId(employee.getEmployeeId())
                .fullName(employee.getFullName())
                .phone(employee.getPhone())
                .address(employee.getAddress())
                .role(employee.getRole())
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public PagingResponse<EmployeeWithOrderResponse> getAllEmployees(Pageable pageable) {
        Page<Object[]> results = employeeRepository.findAllWithOrderCount(pageable);

        List<EmployeeWithOrderResponse> items = results.getContent().stream().map( obj -> {
            Long id =(Long) obj[0];
            String fullName = (String) obj[1];
            String role = (String) obj[2];
            String phone = (String) obj[3];
            String address = (String) obj[4];
            int orderCount = ((Number) obj[5]).intValue();

            return EmployeeWithOrderResponse.builder()
                    .employeeId(id)
                    .fullName(fullName)
                    .role(role)
                    .phone(phone)
                    .address(address)
                    .totalOrder(orderCount)
                    .build();
        }).toList();

        return PagingResponse.of(
                items,
                results.getNumber(),
                results.getTotalPages(),
                results.getTotalElements()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteEmployee(Long employeeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_EXISTED));

        if (employee.getOrders() != null){
            for (Orders order : employee.getOrders()){
                order.setEmployee(null);
            }
        }

        employeeRepository.delete(employee);
    }
}
