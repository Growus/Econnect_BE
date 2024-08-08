package com.growus.econnect.controller;

import com.growus.econnect.base.apiResponse.ApiResponse;
import com.growus.econnect.base.apiResponse.code.status.SuccessStatus;
import com.growus.econnect.dto.user.LoginResponseDTO;
import com.growus.econnect.dto.user.SignUpRequestDTO;
import com.growus.econnect.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

//    @PostMapping("/login")
//    public ApiResponse<?> login(@RequestBody )
}
