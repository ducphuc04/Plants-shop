package com.DucPhuc.Plants_shop.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    USER_EXISTED(1001, "User existed"),
    USER_NOT_FOUND(1002, "User not found"),
    INVALID_PASSWORD(1003, "Invalid password"),
    WRONG_PASSWORD(1004, "Wrong password"),
    EMAIL_IS_NOT_MATCH(1005, "Email is not match"),
    EMAIL_HAS_USED(1006, "Email has used"),
    OTP_INVALID(1007, "Your otp is invalid"),
    OTP_EXPIRED(1008, "Your otp is expired"),
    PASSWORD_IS_USED(1009, "Password is used"),
    PASSWORD_NOT_MATCH(1010, "Password not match"),
    USER_NOT_EXISTED(1011, "User is not existed"),
    PRODUCT_NOT_FOUND(1012, "Product not found"),
    CONFIRM_PASSWORD_NOT_MATCH(1013, "Confirm password not match"),
    PHONE_HAS_USED(1014, "Phone has used"),
    QUANTITY_NOT_ENOUGH(1015, "Quantity not enough"),
    CART_NOT_FOUND(1016, "Cart not found"),
    ITEM_NOT_FOUND(1017, "Item not found"),
    CART_EMPTY(1018, "Cart is empty"),
    OUT_OF_STOCK(1019, "Out of stock"),
    CANNOT_DELETE_ORDER(1020, "Cannot delete order"),
    ORDER_NOT_FOUND(1021, "Order not found"),
    EMPLOYEE_NOT_EXISTED(1022, "Employee not existed"),
    OLD_PASSWORD_IS_WRONG(1023, "Old password is wrong"),
    UNAUTHORIZED(1024, "Unauthorized"),
    PRODUCT_NAME_ALREADY_EXISTS(1025, "Product name already exists"),
    EMPTY_IMAGE(1026, "image is not empty"),
    INVALID_IMAGE_TYPE(1027, "Invalid image type"),
    IMAGE_TOO_LARGE(1028, "Image too large"),
    INVALID_IMAGE(1029, "Invalid image"),
    CAN_NOT_READ_IMAGE(1030, "Can not read image"),
    ORDER_NOT_PENDING(1031, "Order has been solved"),
    EMPLOYEE_NOT_FOUND(1032, "Employee not found"),
    EMPLOYEE_EXISTED(1033, "Employee existed"),
    ACTION_NOT_FOUND(1034, "Action not found"),
    ;


    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
        this.statusCode = HttpStatusCode.valueOf(400);
    }
}
