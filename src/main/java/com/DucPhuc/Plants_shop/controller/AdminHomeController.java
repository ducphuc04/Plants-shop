package com.DucPhuc.Plants_shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminHomeController {
    @GetMapping("/admin-overview")
    public String adminHome() {
        return "admin-overview";
    }

    @GetMapping("/admin-add-product")
    public String adminCreateProduct(@RequestParam(value = "category", required = false) String category) {
        return "admin-add-product";
    }

    @GetMapping("/admin-order")
    public String adminOrder(){
        return "admin-order";
    }

    @GetMapping("/admin-product")
    public String adminProduct(){
        return "admin-product";
    }

    @GetMapping("/admin-edit")
    public String adminEdit(@RequestParam("productId") long productId){
        return "admin-edit";
    }

    @GetMapping("/admin-users")
    public String adminUsers(){
        return "admin-users";
    }

    @GetMapping("/admin-employees")
    public String adminEmployees(){
        return "admin-employees";
    }

    @GetMapping("/admin-create-account")
    public String adminCreateAccount() {
        return "admin-create-account";
    }

    @GetMapping("/admin-manage-account")
    public String adminManageAccount() {
        return "admin-manage-account";
    }
}
