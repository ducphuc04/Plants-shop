package com.DucPhuc.Plants_shop.service;

import com.DucPhuc.Plants_shop.dto.response.OrderDetailResponse;
import com.DucPhuc.Plants_shop.dto.response.OrderResponse;
import com.DucPhuc.Plants_shop.dto.response.PagingResponse;
import com.DucPhuc.Plants_shop.entity.*;
import com.DucPhuc.Plants_shop.exception.AppException;
import com.DucPhuc.Plants_shop.exception.ErrorCode;
import com.DucPhuc.Plants_shop.repository.EmployeeRepository;
import com.DucPhuc.Plants_shop.repository.OrderDetailsRepository;
import com.DucPhuc.Plants_shop.repository.OrderRepository;
import com.DucPhuc.Plants_shop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderDetailsRepository orderDetailsRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_EMPLOYEE')")
    public PagingResponse<OrderResponse> getAllOrders(Pageable pageable){
        Page<Orders> orders = orderRepository.findAll(pageable);

        List<OrderResponse> items = orders.getContent().stream().map(
                order -> OrderResponse.builder()
                        .paymentMethod(order.getPaymentMethod())
                        .totalProduct(order.getTotalProduct())
                        .totalPrice(order.getTotalPrice())
                        .status(order.getStatus())
                        .orderDate(order.getOrderDate())
                        .build()
        ).toList();

        return PagingResponse.of(
                items,
                orders.getNumber(),
                orders.getTotalPages(),
                orders.getTotalElements()
        );
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_EMPLOYEE')")
    public OrderResponse solveOrder(Long orderId, String action){

        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        Employee employee = employeeRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND));

        Orders order = orderRepository.findByOrderId(orderId).orElseThrow(
                () -> new AppException(ErrorCode.ORDER_NOT_FOUND)
        );

        if (!order.getStatus().equals("pending"))
            throw new AppException(ErrorCode.ORDER_NOT_PENDING);

        else{
            if (action.equals("cancel")) {
                order.setStatus("cancelled");
                order.setEmployee(employee);
            }

            else if (action.equals("process")){

                List<OrderDetails> productsOrder = orderDetailsRepository.findByOrder_OrderId(orderId);

                boolean isEnough = true;
                for (OrderDetails p : productsOrder){

                    Product productEntity = productRepository.findByProductId(p.getProduct().getProductId())
                            .orElseThrow( () -> new AppException(ErrorCode.PRODUCT_NOT_FOUND)
                    );

                    if (productEntity.getStock() < p.getQuantity()) {
                        isEnough = false;
                        throw new AppException(ErrorCode.QUANTITY_NOT_ENOUGH);
                    }
                }

                if (isEnough) {
                    System.out.println("********************* ppppppppp ******************");
                    for (OrderDetails p : productsOrder) {
                        System.out.println("********************* hhhhhh ******************");
                        Product productEntity = productRepository.findByProductId(p.getProduct().getProductId())
                                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

                        productEntity.setStock(productEntity.getStock() - p.getQuantity());
                        productRepository.save(productEntity);
                    }
                    System.out.println("********************* ppppppppp ******************");
                    order.setStatus("processed");
                    order.setEmployee(employee);
                }
            }
            else {
                throw new AppException(ErrorCode.ACTION_NOT_FOUND);
            }
            orderRepository.save(order);
        }

        return OrderResponse.builder()
                .orderDate(order.getOrderDate())
                .paymentMethod(order.getPaymentMethod())
                .totalProduct(order.getTotalProduct())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .build();
    }

    public PagingResponse<OrderDetailResponse> getOrderDetail(Long orderId, Pageable pageable){

        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        if (!employeeRepository.existsByUsername(name))
        {
            Orders order = orderRepository.findByOrderId(orderId).orElseThrow(
                    () -> new AppException(ErrorCode.ORDER_NOT_FOUND)
            );

            User user = order.getUser();
            if (!user.getUsername().equals(name))
                throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Page<OrderDetails> orderDetails = orderDetailsRepository.findByOrder_OrderId(orderId, pageable);

        List<OrderDetailResponse> items = orderDetails.getContent().stream().map(
                detail -> {
                    Product product = detail.getProduct();
                    return OrderDetailResponse.builder()
                            .productName(product.getProductName())
                            .quantity(detail.getQuantity())
                            .price(product.getPrice())
                            .totalPrice(product.getPrice() * detail.getQuantity())
                            .build();
                }
        ).toList();

        return PagingResponse.of(
                items,
                orderDetails.getNumber(),
                orderDetails.getTotalPages(),
                orderDetails.getTotalElements()
        );
    }

//    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_EMPLOYEE')")
//    public int getTotalProduct(){
//        return orderRepository.getTotalProductProcessed();
//    }
//
//    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_EMPLOYEE')")
//    public int getTotalPrice(){
//        return orderRepository.getTotalPriceProcessed();
//    }


}
