package com.growus.econnect.service;

import com.growus.econnect.dto.user.MyPageResponseDTO;
import com.growus.econnect.entity.User;
import com.growus.econnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.growus.econnect.base.common.UserAuthorizationUtil.getCurrentUserId;

@Service
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService {
    private final UserRepository userRepository;

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
}
