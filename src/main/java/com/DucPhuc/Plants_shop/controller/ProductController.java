package com.DucPhuc.Plants_shop.controller;

import com.DucPhuc.Plants_shop.dto.response.ApiResponse;
import com.DucPhuc.Plants_shop.dto.response.PagingResponse;
import com.DucPhuc.Plants_shop.dto.response.ProductResponse;
import com.DucPhuc.Plants_shop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/listProduct")
    public ApiResponse<PagingResponse<ProductResponse>> getAllProducts(Pageable pageable) {

        var result = productService.getAllProducts(pageable);

        return ApiResponse.<PagingResponse<ProductResponse>>builder()
                .result(result)
                .build();
    }

    @GetMapping("/detailProduct/{productId}")
    public ApiResponse<ProductResponse> getDetailProduct(@PathVariable("productId") long productId) {
        var result = productService.getDetailProduct(productId);

        return ApiResponse.<ProductResponse>builder()
                .result(result)
                .build();
    }
}
