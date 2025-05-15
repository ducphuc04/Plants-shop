package com.DucPhuc.Plants_shop.util;

import com.DucPhuc.Plants_shop.exception.AppException;
import com.DucPhuc.Plants_shop.exception.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

@Component
public class ImageValidator {
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;
    private static final List<String> ALLOWED_IMAGE_TYPES = List.of("image/jpeg", "image/png", "image/gif");

    public void validate(MultipartFile file){
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.EMPTY_IMAGE);
        }

        String contentType = file.getContentType();
        if (!ALLOWED_IMAGE_TYPES.contains(contentType)){
            throw new AppException(ErrorCode.INVALID_IMAGE_TYPE);
        }

        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new AppException(ErrorCode.IMAGE_TOO_LARGE);
        }

        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null){
                throw  new AppException(ErrorCode.INVALID_IMAGE);
            }
        } catch (IOException e){
            throw new AppException(ErrorCode.CAN_NOT_READ_IMAGE);
        }
    }
}
