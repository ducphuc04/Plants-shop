package com.DucPhuc.Plants_shop.controller;

import com.DucPhuc.Plants_shop.dto.request.AddToCartRequest;
import com.DucPhuc.Plants_shop.dto.request.OrderRequest;
import com.DucPhuc.Plants_shop.dto.request.UpdateUserRequest;
import com.DucPhuc.Plants_shop.dto.request.UserCreationRequest;
import com.DucPhuc.Plants_shop.dto.response.*;
import com.DucPhuc.Plants_shop.service.CartService;
import com.DucPhuc.Plants_shop.service.OrderService;
import com.DucPhuc.Plants_shop.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private CartService cartService;;
    @Autowired
    private OrderService orderService;

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
//        var context = SecurityContextHolder.getContext().getAuthentication();
//        log.info("username" + context.getName());
//        context.getAuthorities().forEach(a -> log.info(a.getAuthority()));

        var result = userService.getUser();
        return ApiResponse.<UserResponse>builder()
                .result(result)
                .build();
    }

    @PutMapping("/update-user/{username}")
    ApiResponse<UserResponse> updateUser(@PathVariable String username, @Valid @RequestBody UpdateUserRequest request)
    {
        var result = userService.updateUser(username, request);
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

    @DeleteMapping("delete-cart-item/{username}/{productId}")
    ApiResponse<String> deleteProduct(@PathVariable String username,
                                       @PathVariable Long productId)
    {
//        System.out.println("DELETE method hit for " + username + ", " + productId);

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

    @PostMapping("/createOrder/{username}")
    ApiResponse<OrderResponse> createOrder(@PathVariable String username)
    {
        var result = cartService.createOrder(username);
        return ApiResponse.<OrderResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/payment/{username}/{orderId}")
    ApiResponse<OrderResponse> payment(@PathVariable String username,
                                       @RequestBody @Valid OrderRequest request,
                                       @PathVariable Long orderId)
    {
        var result = cartService.payment(username, request, orderId);
        return ApiResponse.<OrderResponse>builder()
                .result(result)
                .build();
    }

    @GetMapping("/get-order")
    public ApiResponse<PagingResponse<OrderResponse>> getOrders(Pageable pageable){
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        PagingResponse<OrderResponse> orders = orderService.getAllOrdersForUser(username, pageable);

        return ApiResponse.<PagingResponse<OrderResponse>>builder()
                .result(orders)
                .build();
    }

    @DeleteMapping("/cancel-order/{orderId}")
    public ApiResponse<String> cancelOrder(@PathVariable Long orderId,
                                           @AuthenticationPrincipal Jwt jwt){
        String username = jwt.getSubject();
        orderService.cancelOrder(username, orderId);
        return ApiResponse.<String>builder()
                .result("order has been canceled")
                .build();
    }

    @GetMapping("/order-details/{orderId}")
    public ApiResponse<PagingResponse<OrderDetailResponse>> getOrderDetails(
            @PathVariable Long orderId,
            Pageable pageable,
            @AuthenticationPrincipal Jwt jwt) {

        String username = jwt.getSubject();
        PagingResponse<OrderDetailResponse> details = orderService.getOrderDetail(orderId, pageable);

        return ApiResponse.<PagingResponse<OrderDetailResponse>>builder()
                .result(details)
                .build();
    }

}
