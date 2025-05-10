package com.DucPhuc.Plants_shop.controller;

import com.DucPhuc.Plants_shop.dto.request.CreateEmployeeRequest;
import com.DucPhuc.Plants_shop.dto.request.EmployeeUpdateRequest;
import com.DucPhuc.Plants_shop.dto.response.ApiResponse;
import com.DucPhuc.Plants_shop.dto.response.EmployeeResponse;
import com.DucPhuc.Plants_shop.dto.response.PagingResponse;
import com.DucPhuc.Plants_shop.dto.response.UserSummaryResponse;
import com.DucPhuc.Plants_shop.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

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


}
