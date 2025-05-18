package com.DucPhuc.Plants_shop.configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "ducphuc",
                "api_key", "874882834911873",
                "api_secret", "5pptzdUGuAZRk-lVOqSNUoU_-YA"
        ));
    }
}
