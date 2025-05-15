package com.DucPhuc.Plants_shop.controller;

import com.DucPhuc.Plants_shop.dto.response.ApiResponse;
import com.DucPhuc.Plants_shop.dto.response.PagingResponse;
import com.DucPhuc.Plants_shop.dto.response.ProductResponse;
import com.DucPhuc.Plants_shop.entity.Product;
import com.DucPhuc.Plants_shop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/category")
    public ApiResponse<List<String>> getAllCategories(){
        List<String> result = productService.getAllCategories();

        return ApiResponse.<List<String>>builder()
                .result(result)
                .build();
    }
    @GetMapping("/listProduct")
    public ApiResponse<PagingResponse<ProductResponse>> getAllProducts(@RequestParam(required = false) String category,
                                                                       Pageable pageable) {

        var result = productService.getAllProducts(category, pageable);

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

    @PostMapping("/createProduct")
    public ApiResponse<ProductResponse> createProduct(@RequestBody Product product)
    {
        var result = productService.createProduct(product);

        return ApiResponse.<ProductResponse>builder()
                .result(result)
                .build();
    }

    @DeleteMapping("/deleteProduct/{ProductId}")
    public ApiResponse<String> deleteProduct(@PathVariable("ProductId") long productId)
    {
        productService.deleteProduct(productId);

        return ApiResponse.<String>builder()
                .result("Product has been deleted")
                .build();
    }

    @PutMapping("/updateProduct/{productId}")
    public ApiResponse<ProductResponse> updateProduct(@PathVariable("productId") long productId,
                                                                       @RequestBody Product product)
    {
        var result = productService.updateProduct(productId, product);

        return ApiResponse.<ProductResponse>builder()
                .result(result)
                .build();
    }
}
