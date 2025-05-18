package com.DucPhuc.Plants_shop.service;

import com.DucPhuc.Plants_shop.dto.response.PagingResponse;
import com.DucPhuc.Plants_shop.dto.response.ProductResponse;
import com.DucPhuc.Plants_shop.entity.CartItem;
import com.DucPhuc.Plants_shop.entity.Product;
import com.DucPhuc.Plants_shop.exception.AppException;
import com.DucPhuc.Plants_shop.exception.ErrorCode;
import com.DucPhuc.Plants_shop.repository.CartItemRepository;
import com.DucPhuc.Plants_shop.repository.ProductRepository;
import com.DucPhuc.Plants_shop.util.ImageValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ImageValidator imageValidator;

    @Autowired
    private CartItemRepository cartItemRepository;

    public List<String> getAllCategories() {
        return productRepository.findAllCategories();
    }

    public PagingResponse<ProductResponse> getAllProducts(String category, Pageable pageable) {

        Page<Product> products;
        if (category != null)
            products = productRepository.findByCategory(category, pageable);
        else {
            products = productRepository.findAll(pageable);
        }

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
                .category(product.getCategory())
                .build();
    }

    public ProductResponse getDetailProduct(long ProductId) {
        Product product = productRepository.findById(ProductId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return convertToResponse(product);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    public ProductResponse createProduct(Product product){
        if (productRepository.existsByProductName(product.getProductName())) {
            throw new AppException(ErrorCode.PRODUCT_NAME_ALREADY_EXISTS);
        }

        if (!isValidImage(product.getImage())) {
            throw new AppException(ErrorCode.INVALID_IMAGE);
        }

        product.setCreatedAt(new java.util.Date());
        Product savedProduct = productRepository.save(product);

        return convertToResponse(savedProduct);
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_EMPLOYEE')")
    public ProductResponse createProduct(String name, int price, int stock, String des, String image) {
        if (productRepository.existsByProductName(name)) {
            throw new AppException(ErrorCode.PRODUCT_NAME_ALREADY_EXISTS);
        }

        if (!isValidImage(image)) {
            throw new AppException(ErrorCode.INVALID_IMAGE);
        }

        Product product = Product.builder()
                .productName(name)
                .price(price)
                .stock(stock)
                .description(des)
                .image(image)
                .build();

        product.setCreatedAt(new java.util.Date());
        Product savedProduct = productRepository.save(product);

        return convertToResponse(savedProduct);
    }

    private boolean isValidImage(String image) {
        if (image == null || image.isBlank()) {
            return false;
        }

        String lower = image.toLowerCase();
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".gif");
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_EMPLOYEE')")
    public void deleteProduct(long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        cartItemRepository.deleteAllByProduct_ProductId(productId);

        productRepository.deleteById(productId);
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_EMPLOYEE')")
    public ProductResponse updateProduct(long productId, Product product) {

        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        if (!isValidImage(product.getImage())) {
            throw new AppException(ErrorCode.INVALID_IMAGE);
        }

        if (product != null)
        {
            if (product.getPrice() != null)
                existingProduct.setPrice(product.getPrice());

            if (product.getStock() != null)
                existingProduct.setStock(product.getStock());

            if (product.getDescription() != null)
                existingProduct.setDescription(product.getDescription());

            if (product.getImage() != null) {
                existingProduct.setImage(product.getImage());
            }
        }

        return convertToResponse(existingProduct);
    }
}
