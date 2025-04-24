package com.DucPhuc.Plants_shop.controller;

import com.DucPhuc.Plants_shop.dto.request.UserCreationRequest;
import com.DucPhuc.Plants_shop.dto.response.ApiResponse;
import com.DucPhuc.Plants_shop.dto.response.UserResponse;
import com.DucPhuc.Plants_shop.entity.User;
import com.DucPhuc.Plants_shop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
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

    @GetMapping("/user/{userId}")
    ApiResponse<UserResponse> getUser(@PathVariable("userId") String userId)
    {
        var result = userService.getUser(userId);
        return ApiResponse.<UserResponse>builder()
                .result(result)
                .build();
    }



}
