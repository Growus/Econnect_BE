package com.growus.econnect.controller;

import com.growus.econnect.base.apiResponse.ApiResponse;
import com.growus.econnect.base.apiResponse.code.status.SuccessStatus;
import com.growus.econnect.dto.user.*;
import com.growus.econnect.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ApiResponse<?> signUp(@RequestBody SignUpRequestDTO signUpRequestDTO) {
        Long userId = userService.signUpUser(signUpRequestDTO);
        return ApiResponse.onSuccess(SuccessStatus.SUCCESS_SIGNUP, userId);
    }

    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO user = userService.loginUser(loginRequestDTO);
        return ApiResponse.onSuccess(SuccessStatus.SUCCESS_LOGIN, user);
    }

    @PostMapping("/send-email")
    public ApiResponse<?> sendEmail(@RequestParam String email) throws MessagingException, UnsupportedEncodingException {
        userService.sendAuthenticationCode(email);
        return ApiResponse.onSuccess(SuccessStatus.SUCCESS_SEND_EMAIL, email);
    }

    @GetMapping("/check-code")
    public ApiResponse<?> checkCode(@RequestParam String email, String code) throws MessagingException, UnsupportedEncodingException {
        userService.emailAuthentication(email, code);
        return ApiResponse.onSuccess(SuccessStatus.SUCCESS_CHECK_CODE, email);
    }
}
