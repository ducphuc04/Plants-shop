package com.DucPhuc.Plants_shop.controller;

import com.DucPhuc.Plants_shop.dto.request.AddToCartRequest;
import com.DucPhuc.Plants_shop.dto.request.OrderRequest;
import com.DucPhuc.Plants_shop.dto.request.UpdateUserRequest;
import com.DucPhuc.Plants_shop.dto.request.UserCreationRequest;
import com.DucPhuc.Plants_shop.dto.response.*;
import com.DucPhuc.Plants_shop.service.CartService;
import com.DucPhuc.Plants_shop.service.UserService;
import jakarta.validation.Valid;
import org.hibernate.query.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private CartService cartService;;

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

    @PostMapping("add-to-cart/{username}")
    ApiResponse<AddToCartResponse> add(@PathVariable String username,
                                       @RequestBody @Valid AddToCartRequest request)
    {
        var result = cartService.addToCart(username, request);
        return ApiResponse.<AddToCartResponse>builder()
                .result(result)
                .build();
    }

    @GetMapping("get-cart/{username}")
    ApiResponse<List<CartItemResponse>> getCart(@PathVariable String username)
    {
        var result = cartService.getCartItems(username);
        return ApiResponse.<List<CartItemResponse>>builder()
                .result(result)
                .build();
    }

    @DeleteMapping("delete-cart/{username}/{productId}")
    ApiResponse<String> deleteCart(@PathVariable String username,
                                       @PathVariable Long productId)
    {
        cartService.deleteCartItem(username, productId);
        return ApiResponse.<String>builder()
                .result("product has been deleted")
                .build();
    }

    @PutMapping("update-cart/{username}/{productId}")
    ApiResponse<CartItemResponse> updateCart(@PathVariable String username,
                                       @PathVariable Long productId,
                                       @RequestBody @Valid int request)
    {
        var result = cartService.updateCartItem(username, productId, request);
        return ApiResponse.<CartItemResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/payment/{username}")
    ApiResponse<OrderResponse> payment(@PathVariable String username,
                                       @RequestBody @Valid OrderRequest request)
    {
        var result = cartService.payment(username, request);
        return ApiResponse.<OrderResponse>builder()
                .result(result)
                .build();
    }

    @GetMapping("/get-order")
    public ApiResponse<PagingResponse<OrderResponse>> getOrders(@AuthenticationPrincipal Jwt jwt,
                                                                Pageable pageable){
        String username = jwt.getSubject();

        PagingResponse<OrderResponse> orders = cartService.getAllOrders(username, pageable);

        return ApiResponse.<PagingResponse<OrderResponse>>builder()
                .result(orders)
                .build();
    }

    @DeleteMapping("/delete-order/{orderId}")
    public ApiResponse<String> deleteOrder(@PathVariable Long orderId,
                                           @AuthenticationPrincipal Jwt jwt){
        String username = jwt.getSubject();
        cartService.deleteOrder(username, orderId);
        return ApiResponse.<String>builder()
                .result("order has been deleted")
                .build();
    }
}
