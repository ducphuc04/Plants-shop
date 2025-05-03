package com.DucPhuc.Plants_shop.service;

import com.DucPhuc.Plants_shop.dto.request.UpdateUserRequest;
import com.DucPhuc.Plants_shop.dto.request.UserCreationRequest;
import com.DucPhuc.Plants_shop.dto.request.UserLoginRequest;
import com.DucPhuc.Plants_shop.dto.response.UserResponse;
import com.DucPhuc.Plants_shop.entity.PasswordUsed;
import com.DucPhuc.Plants_shop.entity.User;
import com.DucPhuc.Plants_shop.exception.AppException;
import com.DucPhuc.Plants_shop.exception.ErrorCode;
import com.DucPhuc.Plants_shop.mapper.UserMapper;
import com.DucPhuc.Plants_shop.repository.EmployeeRepository;
import com.DucPhuc.Plants_shop.repository.PasswordUsedRepository;
import com.DucPhuc.Plants_shop.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private PasswordUsedRepository passwordUsedRepository;

    public UserResponse createUser(UserCreationRequest request){
        if (userRepository.existsByUsername(request.getUsername()) || employeeRepository.existsByUsername((request.getUsername())))
            throw new AppException(ErrorCode.USER_EXISTED);

        User user = new User();
        user.setUsername(request.getUsername());
        user.setDateOfCreation(LocalDateTime.now());

        if (request.getPassword().equals(request.getConfirmPassword())){
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        else {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        user = userRepository.save(user);
        PasswordUsed passwordUsed = new PasswordUsed();
        passwordUsed.setPassword(passwordEncoder.encode(request.getPassword()));
        passwordUsed.setUser(user);
        passwordUsedRepository.save(passwordUsed);

        return UserResponse.builder()
                .id((user.getId()))
                .username(user.getUsername())
                .build();
    }

    public UserResponse getUser(){

        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .phone(user.getPhone())
                .address(user.getAddress())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse updateUser(String id, UpdateUserRequest request)
    {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (request.getOldPassword() != null) {
            boolean valid = passwordEncoder.matches(request.getOldPassword(), user.getPassword());

            if (!valid)
                throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
            else {
                if (request.getNewPassword().equals(request.getConfirmPassword())) {
                    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                } else {
                    throw new AppException(ErrorCode.CONFIRM_PASSWORD_NOT_MATCH);
                }
            }
        }

        if (request.getEmail() != null) {
            if (userRepository.existsByEmail(request.getEmail()))
                throw new AppException(ErrorCode.EMAIL_HAS_USED);
            else
                user.setEmail(request.getEmail());
        }

        if (request.getPhone() != null) {
            if (userRepository.existsByPhone(request.getPhone()))
                throw new AppException(ErrorCode.PHONE_HAS_USED);
            else
                user.setPhone(request.getPhone());
        }

        user.setName(request.getName());
        user.setAddress(request.getAddress());

        userRepository.save(user);

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .build();
    }
}
