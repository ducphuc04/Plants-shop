package com.DucPhuc.Plants_shop.controller;

import com.DucPhuc.Plants_shop.dto.response.ApiResponse;
import com.DucPhuc.Plants_shop.dto.response.PagingResponse;
import com.DucPhuc.Plants_shop.dto.response.ProductResponse;
import com.DucPhuc.Plants_shop.entity.Product;
import com.DucPhuc.Plants_shop.service.ProductService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product")
@Slf4j
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private Cloudinary cloudinary;

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

//    @PostMapping("/createProduct")
//    public ApiResponse<ProductResponse> createProduct(@RequestBody Product product)
//    {
//        var result = productService.createProduct(product);
//
//        return ApiResponse.<ProductResponse>builder()
//                .result(result)
//                .build();
//    }

    @PostMapping(value = "/createProduct", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProductResponse> createProduct(@RequestParam String name,
                                                      @RequestParam int price,
                                                      @RequestParam int stock,
                                                      @RequestParam String des,
                                                      @RequestParam String category,
                                                      @RequestParam("image") MultipartFile imageFile
    ){
        try{

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            log.info("User: {}, Authorities: {}", auth.getName(), auth.getAuthorities());

            Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), ObjectUtils.asMap("resource_type", "auto"));

            String imageUrl = uploadResult.get("secure_url").toString();

            var result = productService.createProduct(name, price, stock, des, category, imageUrl);

            return ApiResponse.<ProductResponse>builder()
                    .result(result)
                    .build();
        } catch (IOException e) {
            return ApiResponse.<ProductResponse>builder()
                    .message("Failed to upload image")
                    .build();
        }
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
