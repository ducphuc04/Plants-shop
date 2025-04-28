package com.DucPhuc.Plants_shop.controller;

import com.DucPhuc.Plants_shop.dto.request.UpdateUserRequest;
import com.DucPhuc.Plants_shop.dto.request.UserCreationRequest;
import com.DucPhuc.Plants_shop.dto.response.ApiResponse;
import com.DucPhuc.Plants_shop.dto.response.UserResponse;
import com.DucPhuc.Plants_shop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/registration")
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request)
    {
        var result = userService.createUser(request);
        return ApiResponse.<UserResponse>builder()
                .result(result)
                .build();
    }

    @GetMapping("/get-user-inf")
    ApiResponse<UserResponse> getUser()
    {
        var result = userService.getUser();
        return ApiResponse.<UserResponse>builder()
                .result(result)
                .build();
    }

    @PutMapping("/update-user/{userId}")
    ApiResponse<UserResponse> updateUser(@PathVariable String userId, @Valid @RequestBody UpdateUserRequest request)
    {
        var result = userService.updateUser(userId, request);
        return ApiResponse.<UserResponse>builder()
                .result(result)
                .build();
    }

}
