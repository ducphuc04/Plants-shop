package com.DucPhuc.Plants_shop.controller;

import com.DucPhuc.Plants_shop.configuration.SecurityConfig;
import com.DucPhuc.Plants_shop.dto.request.*;
import com.DucPhuc.Plants_shop.dto.response.*;
import com.DucPhuc.Plants_shop.entity.User;
import com.DucPhuc.Plants_shop.exception.AppException;
import com.DucPhuc.Plants_shop.exception.ErrorCode;
import com.DucPhuc.Plants_shop.repository.EmployeeRepository;
import com.DucPhuc.Plants_shop.repository.UserRepository;
import com.DucPhuc.Plants_shop.service.AuthenticationService;
import com.DucPhuc.Plants_shop.service.ForgotPasswordService;
import com.DucPhuc.Plants_shop.service.UserService;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.Optional;
import java.util.function.Function;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    Function<String, Boolean> isUserFunction;
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    ForgotPasswordService forgotPasswordService;

    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {

        var result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectReponse> authenticate(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {

        var result = authenticationService.introspect(request);

        return ApiResponse.<IntrospectReponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/forgot-password")
    ApiResponse<ForgotPasswordResponse> forgotPasswordResponseApiResponse(@Valid @RequestBody ForgotPasswordRequest request) {

        var result = forgotPasswordService.forgotPassword(request);

        return ApiResponse.<ForgotPasswordResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/reset-password")
    ApiResponse<ResetPasswordResponse> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {

        var result = forgotPasswordService.resetPassword(request);

        return ApiResponse.<ResetPasswordResponse>builder()
                .result(result)
                .build();
    }
}
