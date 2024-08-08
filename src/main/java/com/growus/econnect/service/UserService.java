package com.growus.econnect.service;

import com.growus.econnect.dto.user.SignUpRequestDTO;
import com.growus.econnect.entity.User;

import java.util.Optional;

public interface UserService {
    User loadUserByUsername(String email);
    Long signUpUser(SignUpRequestDTO requestDTO);
    boolean isEmailDuplicate(String email);
    boolean isNicknameDuplicate(String nickname);

}
