package com.growus.econnect.service;

import com.growus.econnect.dto.user.*;
import com.growus.econnect.entity.User;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

public interface UserService {
    User loadUserByUsername(String email);
    Long signUpUser(SignUpRequestDTO requestDTO);
    boolean isEmailDuplicate(String email);
    boolean isNicknameDuplicate(String nickname);
    LoginResponseDTO loginUser(LoginRequestDTO requestDTO);
    void sendAuthenticationCode(String email) throws MessagingException, UnsupportedEncodingException;
    void emailAuthentication(String email, String code);
}
