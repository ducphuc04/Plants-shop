package com.DucPhuc.Plants_shop.configuration;

import com.DucPhuc.Plants_shop.entity.Employee;
import com.DucPhuc.Plants_shop.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Slf4j
public class ApplicationInitConfig {

    @Autowired
    PasswordEncoder passwordEncoder;
    @Bean
    ApplicationRunner applicationRunner(EmployeeRepository employeeRepository){
        return args -> {
            if (employeeRepository.findByUsername("admin").isEmpty()){
                Employee employee = Employee.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123456"))
                        .role("ADMIN")
                        .build();

                employeeRepository.save(employee);
                log.warn("admin has been created");
            }

        };
    }
}
