package com.growus.econnect.service.user;

import com.growus.econnect.dto.user.MyPageResponseDTO;
import com.growus.econnect.entity.User;
import com.growus.econnect.repository.UserRepository;
import com.growus.econnect.service.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.growus.econnect.base.common.UserAuthorizationUtil.getCurrentUserId;

@Service
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService {
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;

    @Override
    public MyPageResponseDTO getUser() {
        Long userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다." + userId));

        return MyPageResponseDTO.builder()
                .profileImage(user.getProfileImage())
                .nickname(user.getNickname())
                .stateMessage(user.getStateMessage())
                .build();
    }

    @Override
    public MyPageResponseDTO updateUserInfo(MultipartFile profileImage, String nickname, String message) {
        Long userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다." + userId));

        // 프로필 이미지 업데이트
        if (profileImage != null && !profileImage.isEmpty()) {
            String oldProfileImageUrl = user.getProfileImage(); // 기존 프로필 이미지 URL 가져오기
            try {
                String newProfileImageUrl = s3Uploader.updateFile(profileImage, oldProfileImageUrl, "profile");
                user.setProfileImage(newProfileImageUrl); // 새로운 프로필 이미지 URL 설정
            } catch (IOException e) {
                throw new RuntimeException("프로필 이미지 업로드 중 오류가 발생했습니다.", e);
            }
        }

        // 닉네임 업데이트
        if (nickname != null && !nickname.isEmpty()) {
            user.setNickname(nickname);
        }

        // 상태 메시지 업데이트
        if (message != null && !message.isEmpty()) {
            user.setStateMessage(message);
        }

        // 변경된 유저 정보 저장
        userRepository.save(user);

        // MyPageResponseDTO를 생성하여 반환
        return MyPageResponseDTO.builder()
                .profileImage(user.getProfileImage())
                .nickname(user.getNickname())
                .stateMessage(user.getStateMessage())
                .build();
    }

}
