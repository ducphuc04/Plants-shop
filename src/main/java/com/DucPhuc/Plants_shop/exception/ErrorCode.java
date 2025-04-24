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
