package com.DucPhuc.Plants_shop.service;

import com.DucPhuc.Plants_shop.dto.request.AuthenticationRequest;
import com.DucPhuc.Plants_shop.dto.request.IntrospectRequest;
import com.DucPhuc.Plants_shop.dto.response.AuthenticationResponse;
import com.DucPhuc.Plants_shop.dto.response.IntrospectReponse;
import com.DucPhuc.Plants_shop.entity.Employee;
import com.DucPhuc.Plants_shop.entity.User;
import com.DucPhuc.Plants_shop.exception.AppException;
import com.DucPhuc.Plants_shop.exception.ErrorCode;
import com.DucPhuc.Plants_shop.repository.EmployeeRepository;
import com.DucPhuc.Plants_shop.repository.UserRepository;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.zaxxer.hikari.pool.HikariProxyCallableStatement;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SINGER_KEY = "";

    public AuthenticationResponse authenticate(AuthenticationRequest request){

        Optional<Employee> employeeOpt = employeeRepository.findByUsername(request.getUsername());

        if (employeeOpt.isPresent()){
            Employee employee = employeeOpt.get();
            boolean authenticated = passwordEncoder.matches(request.getPassword(), employee.getPassword());

            if (!authenticated)
                throw new AppException(ErrorCode.WRONG_PASSWORD);

            String token = generateToken(request.getUsername(), employee.getRole().toUpperCase());

            return AuthenticationResponse.builder()
                    .token(token)
                    .authenticated(true)
                    .role(employee.getRole().toUpperCase())
                    .build();
        }

        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());

        if (userOpt.isPresent()){
            User user = userOpt.get();
            boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

            if (!authenticated)
                throw new AppException(ErrorCode.WRONG_PASSWORD);

            String token = generateToken(request.getUsername(), "USER");

            return AuthenticationResponse.builder()
                    .token(token)
                    .authenticated(true)
                    .role("USER")
                    .build();
        }

        throw new AppException(ErrorCode.USER_NOT_FOUND);
    }


    private String generateToken(String username, String role)  {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .issuer("ducphuc.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                ))
                .claim("scope", role)
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SINGER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch(JOSEException e){
            System.out.println("Cannot create token");
            throw new RuntimeException(e);
        }
    }

    public IntrospectReponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();

        JWSVerifier verifier = new MACVerifier(SINGER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        return IntrospectReponse.builder()
                .valid(expirationTime.after(new Date()) && verified)
                .build();

    }
}
