package com.growus.econnect.controller;

import com.growus.econnect.base.apiResponse.ApiResponse;
import com.growus.econnect.base.apiResponse.code.status.SuccessStatus;
import com.growus.econnect.dto.user.MyPageResponseDTO;
import com.growus.econnect.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/my-page")
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageService mypageService;

    @GetMapping("/")
    public ApiResponse<?> getMyPage() {
        MyPageResponseDTO user = mypageService.getUser();
        return ApiResponse.onSuccess(SuccessStatus.SUCCESS_SIGNUP, user);
    }
}
