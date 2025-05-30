package com.DucPhuc.Plants_shop.service;

import com.DucPhuc.Plants_shop.dto.response.PagingResponse;
import com.DucPhuc.Plants_shop.dto.response.ProductResponse;
import com.DucPhuc.Plants_shop.entity.CartItem;
import com.DucPhuc.Plants_shop.entity.OrderDetails;
import com.DucPhuc.Plants_shop.entity.Orders;
import com.DucPhuc.Plants_shop.entity.Product;
import com.DucPhuc.Plants_shop.exception.AppException;
import com.DucPhuc.Plants_shop.exception.ErrorCode;
import com.DucPhuc.Plants_shop.repository.CartItemRepository;
import com.DucPhuc.Plants_shop.repository.OrderDetailsRepository;
import com.DucPhuc.Plants_shop.repository.ProductRepository;
import com.DucPhuc.Plants_shop.util.ImageValidator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

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
                .productId(product.getProductId())
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

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ProductResponse createProduct(String name, int price, int stock, String des, String category, String image) {
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
                .category(category)
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

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @Transactional
    public void deleteProduct(long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        if (product.getCartItems() != null){
            for (CartItem cartItem : new ArrayList<>(product.getCartItems())){
                cartItem.setProduct(null);
                cartItemRepository.delete(cartItem);
            }
            product.getCartItems().clear();
        }

        if (product.getOrderDetailsList() != null){
            for (OrderDetails detail : new ArrayList<>(product.getOrderDetailsList())){
                detail.setProduct(null);
                orderDetailsRepository.delete(detail);
            }
            product.getOrderDetailsList().clear();
        }

        productRepository.delete(product);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @Transactional
    public ProductResponse updateProduct(long productId, Product product) {

        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        if (product.getPrice() != null)
            existingProduct.setPrice(product.getPrice());

        if (product.getStock() != null)
            existingProduct.setStock(product.getStock());

        if (product.getDescription() != null)
            existingProduct.setDescription(product.getDescription());


        if (existingProduct.getOrderDetailsList() != null) {
            for (OrderDetails detail : existingProduct.getOrderDetailsList()) {
                if (detail.getOrder() != null && !detail.getOrder().getStatus().equals("processed")) {
                    if (product.getPrice() != null) {
                        Orders order = detail.getOrder();
                        order.setTotalPrice(order.getTotalPrice() + detail.getQuantity() * (product.getPrice() - detail.getPrice()));
                        System.out.println("***********");
                        detail.setPrice(product.getPrice());
                    }
                }
            }
        }

        if (product.getStock() != null)
            existingProduct.setStock(product.getStock());

        if (product.getDescription() != null)
            existingProduct.setDescription(product.getDescription());

        if (product.getPrice() != null)
            existingProduct.setPrice(product.getPrice());

        return convertToResponse(existingProduct);
    }
}
