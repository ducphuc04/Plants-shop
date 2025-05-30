package com.DucPhuc.Plants_shop.controller;

import com.DucPhuc.Plants_shop.dto.request.CreateEmployeeRequest;
import com.DucPhuc.Plants_shop.dto.request.EmployeeUpdateRequest;
import com.DucPhuc.Plants_shop.dto.response.*;
import com.DucPhuc.Plants_shop.service.AdminService;
import com.DucPhuc.Plants_shop.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/listUser")
    public ApiResponse<PagingResponse<UserSummaryResponse>> getAllUsers(Pageable pageable) {

        var result = adminService.getAllUsers(pageable);
        return ApiResponse.<PagingResponse<UserSummaryResponse>>builder()
                .result(result)
                .build();
    }

    @PostMapping("/createEmployee")
    public ApiResponse<EmployeeResponse> createEmployee(@RequestBody @Valid CreateEmployeeRequest request) {

        var result = adminService.createEmployee(request);
        return ApiResponse.<EmployeeResponse>builder()
                .result(result)
                .build();
    }

    @PutMapping("/updateEmployee/{username}")
    public ApiResponse<EmployeeResponse> updateEmployee(@PathVariable String username,
                                                         @RequestBody EmployeeUpdateRequest request) {
        return ApiResponse.<EmployeeResponse>builder()
                .result(adminService.updateEmployee(username, request))
                .build();
    }

    @GetMapping("getAllEmployees")
    public ApiResponse<PagingResponse<EmployeeWithOrderResponse>> getAllEmployees(Pageable pageable) {

        var result = adminService.getAllEmployees(pageable);
        return ApiResponse.<PagingResponse<EmployeeWithOrderResponse>>builder()
                .result(result)
                .build();
    }

    @DeleteMapping("/deleteEmployee/{employeeId}")
    public ApiResponse<String> deleteEmployee(@PathVariable Long employeeId) {

        adminService.deleteEmployee(employeeId);

        return ApiResponse.<String>builder()
                .result(employeeId + " has been deleted")
                .build();
    }

    @GetMapping("/getOrders")
    public ApiResponse<PagingResponse<OrderResponse>> getAllOrders(Pageable pageable, @RequestParam(required = false) String status) {
        var result = orderService.getAllOrders(pageable, status);

        return ApiResponse.<PagingResponse<OrderResponse>>builder()
                .result(result)
                .build();
    }

    @PostMapping("/solveOrder/{orderId}")
    public ApiResponse<OrderResponse> solveOrder(@PathVariable Long orderId,
                                                 @RequestParam String action) {
        var result = orderService.solveOrder(orderId, action);

        return ApiResponse.<OrderResponse>builder()
                .result(result)
                .build();
    }

    @GetMapping("/orderDetail/{orderId}")
    public ApiResponse<PagingResponse<OrderDetailResponse>> getOrderDetail(@PathVariable Long orderId,
                                                                           Pageable pageable) {

        var result = orderService.getOrderDetail(orderId, pageable);

        return ApiResponse.<PagingResponse<OrderDetailResponse>>builder()
                .result(result)
                .build();
    }

    @GetMapping("/getEmployee")
    public ApiResponse<EmployeeResponse> getEmployee(){
        return ApiResponse.<EmployeeResponse>builder()
                .result(adminService.getEmployee())
                .build();
    }

}
