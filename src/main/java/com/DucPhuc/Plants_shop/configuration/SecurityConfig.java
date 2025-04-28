package com.DucPhuc.Plants_shop.configuration;

import com.DucPhuc.Plants_shop.dto.response.AuthenticationResponse;
import com.DucPhuc.Plants_shop.entity.Employee;
import com.DucPhuc.Plants_shop.entity.User;
import com.DucPhuc.Plants_shop.exception.AppException;
import com.DucPhuc.Plants_shop.exception.ErrorCode;
import com.DucPhuc.Plants_shop.repository.EmployeeRepository;
import com.DucPhuc.Plants_shop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import javax.crypto.spec.SecretKeySpec;
import java.util.function.Function;

import static org.springframework.security.oauth2.jose.jws.JwsAlgorithms.HS512;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    UserRepository userRepository;
    private final String[] PUBLIC_ENDPOINTS = {"/auth/login", "/auth/introspect", "/users", "/registration", "/auth/forgot-password", "/auth/reset-password"};
    @Value("${jwt.signerKey}")
    private String SINGER_KEY;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public Function<String, Boolean> isUserFunction() {
        return username -> userRepository.findByUsername(username).isPresent();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.authorizeHttpRequests(request ->
                request.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.GET, "/product/**").permitAll()
                        .anyRequest().authenticated());

        httpSecurity.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder()))
        );

        httpSecurity.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();

    }

    @Bean
    JwtDecoder jwtDecoder(){
        SecretKeySpec secretKeySpec = new SecretKeySpec(SINGER_KEY.getBytes(), HS512);
        return NimbusJwtDecoder
                .withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

}
