package com.growus.econnect.controller;

import com.growus.econnect.base.apiResponse.ApiResponse;
import com.growus.econnect.base.apiResponse.code.status.ErrorStatus;
import com.growus.econnect.base.apiResponse.code.status.SuccessStatus;
import com.growus.econnect.base.apiResponse.exception.InvalidPasswordException;
import com.growus.econnect.dto.user.MyPageResponseDTO;
import com.growus.econnect.dto.user.UpdatePasswordRequestDTO;
import com.growus.econnect.service.user.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/my-page")
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageService mypageService;

    @GetMapping("")
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

    @PatchMapping("/password")
    public ApiResponse<?> updatePassword(@RequestBody UpdatePasswordRequestDTO updatePasswordRequestDTO) {
        try {
            Long userId = mypageService.updatePassword(updatePasswordRequestDTO.getCurrentPassword(), updatePasswordRequestDTO.getNewPassword());
            return ApiResponse.onSuccess(SuccessStatus.SUCCESS_UPDATE_PASSWORD, userId);
        } catch (InvalidPasswordException e) {
            return ApiResponse.onFailure(ErrorStatus.INVALID_CURRENT_PASSWORD, null);
        } catch (Exception e) {
            // 일반적인 예외 처리
            return ApiResponse.onFailure(ErrorStatus._INTERNAL_SERVER_ERROR, null);
        }
    }
}
