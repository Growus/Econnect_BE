package com.growus.econnect.service.user;

import com.growus.econnect.dto.user.*;
import com.growus.econnect.entity.User;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;
import com.growus.econnect.dto.user.SignUpRequestDTO;

public interface UserService {
    User loadUserByUsername(String email);
    Long signUpUser(SignUpRequestDTO requestDTO);
    boolean isEmailDuplicate(String email);
    boolean isNicknameDuplicate(String nickname);
    void findPassword(String email, String password);
    LoginResponseDTO loginUser(LoginRequestDTO requestDTO);
    void sendAuthenticationCode(String email) throws MessagingException, UnsupportedEncodingException;
    void emailAuthentication(String email, String code);
}
