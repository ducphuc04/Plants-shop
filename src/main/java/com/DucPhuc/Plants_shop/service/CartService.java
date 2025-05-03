package com.DucPhuc.Plants_shop.service;

import com.DucPhuc.Plants_shop.dto.request.AddToCartRequest;
import com.DucPhuc.Plants_shop.dto.request.OrderRequest;
import com.DucPhuc.Plants_shop.dto.response.AddToCartResponse;
import com.DucPhuc.Plants_shop.dto.response.CartItemResponse;
import com.DucPhuc.Plants_shop.dto.response.OrderResponse;
import com.DucPhuc.Plants_shop.dto.response.PagingResponse;
import com.DucPhuc.Plants_shop.entity.*;
import com.DucPhuc.Plants_shop.exception.AppException;
import com.DucPhuc.Plants_shop.exception.ErrorCode;
import com.DucPhuc.Plants_shop.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public AddToCartResponse addToCart(String username, AddToCartRequest request)
    {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Product product = productRepository.findByProductId(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        Cart cart = cartRepository.findByUser(user)
                .orElse(Cart.builder().user(user).build());

        cartRepository.save(cart);
        if (request.getQuantity() > product.getStock())
            throw new AppException(ErrorCode.QUANTITY_NOT_ENOUGH);
        else {
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
                    .productId(cartItem.getProduct().getProductId())
                    .quantity(cartItem.getQuantity())
                    .valid(true)
                    .build();
        }
    }

    public List<CartItemResponse> getCartItems(String username){
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

    public void deleteCartItem(String username, long productId){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        CartItem cartItem = cartItemRepository.findByCartAndProduct_ProductId(cart, productId)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_FOUND));

        cartItemRepository.delete(cartItem);
    }

    public CartItemResponse updateCartItem (String username, long productId, int newQuantity){
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
                .productName(cartItem.getProduct().getProductName())
                .image(cartItem.getProduct().getImage())
                .quantity(cartItem.getQuantity())
                .price(cartItem.getProduct().getPrice()* cartItem.getQuantity())
                .build();
    }

    public OrderResponse payment(String username, OrderRequest request){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        Date paymentDate = new Date();
        if (request.getPaymentMethod().equals("BANKING"))
            paymentDate.setTime(paymentDate.getTime());
        else
            paymentDate = null;
        Orders order = Orders.builder()
                .user(user)
                .orderDate(new Date())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .paymentMethod(request.getPaymentMethod())
                .paymentDate(paymentDate)
                .totalProduct(cart.getCartItems().size())
                .status("pending")
                .build();
        if (cart.getCartItems().isEmpty())
            throw new AppException(ErrorCode.CART_EMPTY);

        int total = 0;

        for (CartItem item : cart.getCartItems()){
            Product product = item.getProduct();

            if (product.getStock() < item.getQuantity())
                throw new AppException(ErrorCode.OUT_OF_STOCK);
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);

            total += product.getPrice() * item.getQuantity();

            OrderDetails.builder()
                    .order(order)
                    .product(product)
                    .quantity(item.getQuantity())
                    .build();
        }

        cart.getCartItems().clear();
        cartRepository.save(cart);
        order.setTotalPrice(total);
        orderRepository.save(order);

        return OrderResponse.builder()
                .orderDate(order.getOrderDate())
                .totalPrice(total)
                .totalProduct(cart.getCartItems().size())
                .paymentMethod(order.getPaymentMethod())
                .build();
    }

    public PagingResponse<OrderResponse> getAllOrders(String username, Pageable pageable)
    {
        Page<Orders> orders = orderRepository.findByUser_username(username, pageable);

        List<OrderResponse> orderResponses = orders.stream()
                .map(order -> OrderResponse.builder()
                        .orderDate(order.getOrderDate())
                        .totalPrice(order.getTotalPrice())
                        .totalProduct(order.getTotalProduct())
                        .paymentMethod(order.getPaymentMethod())
                        .build())
                .collect(Collectors.toList());

        return PagingResponse.of(
                orderResponses,
                orders.getNumber(),
                orders.getTotalPages(),
                orders.getTotalElements()
        );
    }

    public void deleteOrder(String username, Long orderId)
    {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Orders order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

       if (order.getStatus().equals("pending"))
           orderRepository.delete(order);
       else
           throw new AppException(ErrorCode.CANNOT_DELETE_ORDER);
    }
}
