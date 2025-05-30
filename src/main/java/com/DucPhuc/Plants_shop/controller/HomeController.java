package com.DucPhuc.Plants_shop.controller;

import com.DucPhuc.Plants_shop.dto.response.CartItemResponse;
import com.DucPhuc.Plants_shop.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private CartService cartService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/features")
    public String features() {
        return "features";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/manage-account")
    public String manageAccount() {
        return "manage-account";
    }

    @GetMapping("/product-detail-page")
    public String detailProduct(@RequestParam("product") Long id) {
        return "product-detail-page";
    }

    @GetMapping("/contact-us")
    public String contactUs() {
        return "contact-us";
    }

    @GetMapping("/shopping-cart")
    public String shoppingCart() {
        return "shopping-cart";
    }

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @GetMapping("/forget-password")
    public String forgetPassword() {
        return "forget-password";
    }

    @GetMapping("/checkout")
    public String checkout(@RequestParam("orderId") Long orderId){
        return "checkout";
    }

    @GetMapping("/history-order")
    public String histtoryOrder(){
        return "history-order";
    }

}

