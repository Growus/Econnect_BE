package com.growus.econnect.controller;

import com.growus.econnect.base.apiResponse.ApiResponse;
import com.growus.econnect.base.apiResponse.code.status.SuccessStatus;
import com.growus.econnect.dto.user.MyPageResponseDTO;
import com.growus.econnect.service.user.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/my-page")
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageService mypageService;

    @GetMapping("/")
    public ApiResponse<?> getMyPage() {
        MyPageResponseDTO user = mypageService.getUser();
        return ApiResponse.onSuccess(SuccessStatus.SUCCESS_GET_MYPAGE, user);
    }

    @PatchMapping(value = "/info", consumes = "multipart/form-data")
    public ApiResponse<?> updateUserInfo(@RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
                                         @RequestPart(value = "nickname", required = false) String nickname,
                                         @RequestPart(value = "message", required = false) String message) {
        MyPageResponseDTO user = mypageService.updateUserInfo(profileImage, nickname, message);
        return ApiResponse.onSuccess(SuccessStatus.SUCCESS_UPDATE_USER_INFO, user);
    }
}
