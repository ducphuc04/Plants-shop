package com.DucPhuc.Plants_shop.service;

import com.DucPhuc.Plants_shop.dto.request.ForgotPasswordRequest;
import com.DucPhuc.Plants_shop.dto.request.ResetPasswordRequest;
import com.DucPhuc.Plants_shop.dto.response.ForgotPasswordResponse;
import com.DucPhuc.Plants_shop.dto.response.ResetPasswordResponse;
import com.DucPhuc.Plants_shop.entity.OtpResetToken;
import com.DucPhuc.Plants_shop.entity.PasswordUsed;
import com.DucPhuc.Plants_shop.entity.User;
import com.DucPhuc.Plants_shop.exception.AppException;
import com.DucPhuc.Plants_shop.exception.ErrorCode;
import com.DucPhuc.Plants_shop.repository.OtpResetTokenRepository;
import com.DucPhuc.Plants_shop.repository.PasswordUsedRepository;
import com.DucPhuc.Plants_shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ForgotPasswordService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private OtpResetTokenRepository otpResetTokenRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Function<String, Boolean> isUserFunction;
    @Autowired
    private PasswordUsedRepository passwordUsedRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private boolean checkValid(ForgotPasswordRequest request){
        String username = request.getUsername();
        String emailReq = request.getEmail();
        Optional<User> userOtp = userRepository.findByUsername(username);

        if (!isUserFunction.apply(username)) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        else{
            String emailRepo = userOtp.get().getEmail();
            System.out.println(userRepository.existsByEmail(emailReq));
//            if (emailRepo != null)
//                System.out.println(emailRepo.isEmpty());


            if (emailRepo != null && !emailRepo.isEmpty() && !emailRepo.equals(emailReq)){
                throw new AppException(ErrorCode.EMAIL_IS_NOT_MATCH);
            }
            else {

                if ((emailRepo == null || emailRepo.isEmpty()) && userRepository.existsByEmail(emailReq)){
                    throw new AppException((ErrorCode.EMAIL_HAS_USED));
                }
                else {
                    Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
                    userOpt.get().setEmail(emailRepo);
                    return true;
                }
            }
        }
    }

    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request){
        if (!checkValid(request)) {
            return ForgotPasswordResponse.builder()
                    .valid(false)
                    .message("Invalid username or email")
                    .build();
        }
        else {
            SimpleMailMessage message = new SimpleMailMessage();
            int otp = generateOtp();

            while (otpResetTokenRepository.existsByOtp(String.valueOf(otp))){
                otp = generateOtp();
            }

            message.setTo(request.getEmail());
            message.setFrom("minato111ts@gmail.com");
            message.setSubject("Reset Your Password");
            message.setText("OTP for Forgot Password request:" + otp);

            mailSender.send(message);

            Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
            Optional<OtpResetToken> existingToken = otpResetTokenRepository.findByUserId(userOpt.get().getId());

            OtpResetToken otpResetToken = new OtpResetToken();
            if (existingToken.isPresent()){
                otpResetToken = existingToken.get();
                otpResetToken.setOtp(String.valueOf(otp));
                otpResetToken.setExpiryDate(LocalDateTime.now().plusMinutes(5));

            }
            else {
                otpResetToken.setOtp(String.valueOf(otp));
                otpResetToken.setUser(userOpt.get());
                otpResetToken.setExpiryDate(LocalDateTime.now().plusMinutes(5));
            }

            otpResetTokenRepository.save(otpResetToken);
            userOpt.get().setEmail(request.getEmail());
            userRepository.save(userOpt.get());
            return ForgotPasswordResponse.builder()
                    .valid(true)
                    .message("OTP has been sent to your email")
                    .build();
        }
    }

    private int generateOtp(){
        Random random = new Random();
        return random.nextInt(100_000,999999);
    }

    public ResetPasswordResponse resetPassword(ResetPasswordRequest request){

        OtpResetToken otpResetToken = otpResetTokenRepository.findByOtp(request.getOtp())
                .orElseThrow(() -> new AppException(ErrorCode.OTP_INVALID));
        if (otpResetToken.getExpiryDate().isBefore(LocalDateTime.now())){
            otpResetTokenRepository.deleteById(otpResetToken.getId());
            throw new AppException(ErrorCode.OTP_EXPIRED);
        }
        else {
            Optional<User> userOpt = userRepository.findByUsername(otpResetToken.getUser().getUsername());
            PasswordUsed passwordUsed = new PasswordUsed();


            if (passwordEncoder.matches(request.getNewPassword(), userOpt.get().getPassword())
                || isPasswordUsed(userOpt.get(), request.getNewPassword())){
                throw new AppException((ErrorCode.PASSWORD_IS_USED));
            }
            else if (!request.getNewPassword().equals(request.getConfirmPassword())){
                throw new AppException((ErrorCode.PASSWORD_NOT_MATCH));
            }
            else {
                userOpt.get().setPassword(request.getNewPassword());
                passwordUsed.setPassword(passwordEncoder.encode(request.getNewPassword()));
                passwordUsed.setUser(userOpt.get());
            }

            otpResetTokenRepository.deleteById(otpResetToken.getId());
            userRepository.save(userOpt.get());
            passwordUsedRepository.save(passwordUsed);
            return ResetPasswordResponse.builder()
                    .valid(true)
                    .message("Password has been reset successfully")
                    .build();
        }
    }

    private Boolean isPasswordUsed(User user, String password) {

        List<PasswordUsed> passwordUsedList = passwordUsedRepository.findByUser_Username(user.getUsername());

        for (PasswordUsed passwordUsed : passwordUsedList) {
            if (passwordEncoder.matches(password, passwordUsed.getPassword())) {
                return true;
            }
        }
        return false;
    }
}
