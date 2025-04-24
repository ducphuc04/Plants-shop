package com.DucPhuc.Plants_shop.service;

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

    public UserResponse getUser(String id){
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return UserResponse.builder()
                .username(user.getUsername())
                .phone(user.getPhone())
                .address(user.getAddress())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}
