package com.DucPhuc.Plants_shop.service;

import com.DucPhuc.Plants_shop.dto.response.PagingResponse;
import com.DucPhuc.Plants_shop.dto.response.ProductResponse;
import com.DucPhuc.Plants_shop.entity.Product;
import com.DucPhuc.Plants_shop.exception.AppException;
import com.DucPhuc.Plants_shop.exception.ErrorCode;
import com.DucPhuc.Plants_shop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public PagingResponse<ProductResponse> getAllProducts(Pageable pageable) {

        Page<Product> products = productRepository.findAll(pageable);

        List<ProductResponse> productResponses = products.getContent()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return PagingResponse.of(
                productResponses,
                products.getNumber(),
                products.getTotalPages(),
                products.getTotalElements()
        );
    }

    private ProductResponse convertToResponse(Product product) {
        return ProductResponse.builder()
                .productName(product.getProductName())
                .price(product.getPrice())
                .description(product.getDescription())
                .image(product.getImage())
                .build();
    }

    public ProductResponse getDetailProduct(long ProductId) {
        Product product = productRepository.findById(ProductId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return convertToResponse(product);
    }
}
