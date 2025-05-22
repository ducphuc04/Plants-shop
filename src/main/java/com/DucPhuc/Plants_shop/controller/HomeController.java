package com.DucPhuc.Plants_shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(){
        return "index";
    }

    @GetMapping("/index")
    public String index(){
        return "index";
    }

    @GetMapping("/features")
    public String features(){
        return "features";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/register")
    public String register(){
        return "register";
    }

    @GetMapping("/manage-account")
    public String manageAccount(){
        return "manage-account";
    }

    @GetMapping("/shopping-cart")
    public String shoppingCart(){
        return "shopping-cart";
    }

    @GetMapping("/product-detail-page")
    public String detailProduct(@RequestParam("product") Long id){
        return "product-detail-page";
    }

    @GetMapping("contact-us")
    public String contactUs(){
        return "contact-us";
    }
}
