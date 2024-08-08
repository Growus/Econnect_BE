package com.growus.econnect.service;

import com.growus.econnect.dto.user.SignUpRequestDTO;
import com.growus.econnect.entity.User;
import com.growus.econnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public User loadUserByUsername(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException((email)));
    }

    @Override
    public Long signUpUser(SignUpRequestDTO requestDTO) {
        if (isEmailDuplicate(requestDTO.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }
        if (isNicknameDuplicate(requestDTO.getNickName())) {
            throw new IllegalArgumentException("Nickname is already in use");
        }

        User user = User.builder()
                .email(requestDTO.getEmail())
                .nickname(requestDTO.getNickName())
                .password(bCryptPasswordEncoder.encode(requestDTO.getPassword()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return userRepository.save(user).getUserId();
    }

    @Override
    public boolean isEmailDuplicate(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public boolean isNicknameDuplicate(String nickname) {
        return userRepository.findByNickname(nickname).isPresent();
    }
}
