package com.DucPhuc.Plants_shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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
}
