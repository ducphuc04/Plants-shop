package com.DucPhuc.Plants_shop.service;

import com.DucPhuc.Plants_shop.dto.request.AddToCartRequest;
import com.DucPhuc.Plants_shop.dto.request.OrderRequest;
import com.DucPhuc.Plants_shop.dto.response.*;
import com.DucPhuc.Plants_shop.entity.*;
import com.DucPhuc.Plants_shop.exception.AppException;
import com.DucPhuc.Plants_shop.exception.ErrorCode;
import com.DucPhuc.Plants_shop.repository.*;
import org.hibernate.query.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    CartItemRepository cartItemRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderDetailsRepository orderDetailsRepository;

    public AddToCartResponse addToCart(String username, AddToCartRequest request)
    {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        if (!name.equals(username))
            throw new AppException(ErrorCode.UNAUTHORIZED);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Product product = productRepository.findByProductId(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        Cart cart = cartRepository.findByUser(user)
                .orElse(Cart.builder().user(user).build());

        cartRepository.save(cart);

        CartItem cartItem = cartItemRepository.findByCartAndProduct_ProductId(cart, product.getProductId())
                .map(item -> {
                    item.setQuantity(item.getQuantity() + request.getQuantity());
                    return item;
                })
                .orElseGet(() -> CartItem.builder()
                        .cart(cart)
                        .product(product)
                        .quantity(request.getQuantity())
                        .build());

        cartItemRepository.save(cartItem);


        return AddToCartResponse.builder()
                .productName(product.getProductName())
                .quantity(cartItem.getQuantity())
                .price(product.getPrice())
                .totalPrice(product.getPrice() * cartItem.getQuantity())
                .build();

    }

    public List<CartItemResponse> getCartItems(String username){

        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        if (!name.equals(username))
            throw new AppException(ErrorCode.UNAUTHORIZED);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        return cart.getCartItems().stream()
                .map(item -> CartItemResponse.builder()
                        .productId(item.getProduct().getProductId())
                        .quantity(item.getQuantity())
                        .productName(item.getProduct().getProductName())
                        .price(item.getProduct().getPrice() * item.getQuantity())
                        .image(item.getProduct().getImage())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteCartItem(String username, long productId){

        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        if (!name.equals(username))
            throw new AppException(ErrorCode.UNAUTHORIZED);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        CartItem cartItem = cartItemRepository.findByCartAndProduct_ProductId(cart, productId)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_FOUND));

        cart.getCartItems().remove(cartItem);
        cartRepository.save(cart);

    }

    public CartItemResponse updateCartItem (String username, long productId, int newQuantity){

        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        if (!name.equals(username))
            throw new AppException(ErrorCode.UNAUTHORIZED);

        if (newQuantity < 0)
            throw new AppException(ErrorCode.QUANTITY_NOT_ENOUGH);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getProductId() == productId)
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_FOUND));

        cartItem.setQuantity(newQuantity);
        cartItemRepository.save(cartItem);

        return CartItemResponse.builder()
                .productId(productId)
                .productName(cartItem.getProduct().getProductName())
                .image(cartItem.getProduct().getImage())
                .quantity(cartItem.getQuantity())
                .price(cartItem.getProduct().getPrice()* cartItem.getQuantity())
                .build();
    }

    public OrderResponse createOrder(String username){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        if (!name.equals(username))
            throw new AppException(ErrorCode.UNAUTHORIZED);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        if (cart.getCartItems().isEmpty())
            throw new AppException(ErrorCode.CART_EMPTY);

        Orders orders = Orders.builder()
                .user(user)
                .orderDate(new Date())
                .status("unpaid")
                .totalProduct(cart.getCartItems().size())
                .build();

        int total = 0;
        for (CartItem item : cart.getCartItems()){
            Product product = item.getProduct();

            total += product.getPrice() * item.getQuantity();
        }

        orders.setTotalPrice(total);
        orderRepository.save(orders);

        for (CartItem item : cart.getCartItems()){
            OrderDetails orderDetails = OrderDetails.builder()
                    .order(orders)
                    .product(item.getProduct())
                    .quantity(item.getQuantity())
                    .price(item.getProduct().getPrice())
                    .build();
            orderDetailsRepository.save(orderDetails);
        }

        cart.getCartItems().clear();
        cartRepository.save(cart);

        return OrderResponse.builder()
                .orderId(orders.getOrderId())
                .orderDate(orders.getOrderDate())
                .totalPrice(total)
                .totalProduct(orders.getTotalProduct())
                .status(orders.getStatus())
                .build();
    }

    public OrderResponse payment(String username, OrderRequest request, Long orderId){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        if (!name.equals(username))
            throw new AppException(ErrorCode.UNAUTHORIZED);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Orders order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (order.getStatus().equals("unpaid") || order.getStatus().equals("cancelled")){
            order.setStatus("pending");
            order.setPaymentMethod(request.getPaymentMethod());
            if (order.getPaymentMethod().equals("BANKING"))
                order.setPaymentDate(new Date());
            order.setName(request.getName());
            order.setAddress(request.getAddress());
            order.setPhone(request.getPhone());
            order.setEmail(request.getEmail());
            orderRepository.save(order);
        }
        else
            throw new AppException(ErrorCode.CANNOT_PAY_ORDER);

        return OrderResponse.builder()
                .orderDate(order.getOrderDate())
                .totalPrice(order.getTotalPrice())
                .totalProduct(order.getTotalProduct())
                .paymentMethod(order.getPaymentMethod())
                .status(order.getStatus())
                .build();
    }
}
