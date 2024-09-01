package com.growus.econnect.service.user;

import com.growus.econnect.dto.user.MyPageResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface MyPageService {
    MyPageResponseDTO getUser();

    MyPageResponseDTO updateUserInfo(MultipartFile profileImage, String nickname, String message);
}
