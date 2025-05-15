package com.DucPhuc.Plants_shop.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ProductRequest {
    String productName;
    String description;
    int price;
    String category;
    int stock;
    MultipartFile image;
}
