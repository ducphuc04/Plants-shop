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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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
                .stock(product.getStock())
                .image(product.getImage())
                .type(product.getType())
                .build();
    }

    public ProductResponse getDetailProduct(long ProductId) {
        Product product = productRepository.findById(ProductId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return convertToResponse(product);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    public ProductResponse createProduct(Product product){
        if (productRepository.existsByProductName(product.getProductName())) {
            throw new AppException(ErrorCode.PRODUCT_NAME_ALREADY_EXISTS);
        }
        product.setCreatedAt(new java.util.Date());
        Product savedProduct = productRepository.save(product);

        return convertToResponse(savedProduct);
    }
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    public void deleteProduct(long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        productRepository.deleteById(productId);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    public ProductResponse updateProduct(long productId, Product product) {

        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        existingProduct.setProductName(product.getProductName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setType(product.getType());
        existingProduct.setStock(product.getStock());
        existingProduct.setImage(product.getImage());
        existingProduct.setDescription(product.getDescription());

        productRepository.save(existingProduct);

        return convertToResponse(existingProduct);
    }

}
