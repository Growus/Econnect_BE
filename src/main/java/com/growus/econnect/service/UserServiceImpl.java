package com.growus.econnect.service;

import com.growus.econnect.base.config.RedisEmailAuthentication;
import com.growus.econnect.base.jwt.JwtProvider;
import com.growus.econnect.dto.user.*;
import com.growus.econnect.entity.User;
import com.growus.econnect.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MailService mailService;
    private final RedisService redisService;
    private final JwtProvider jwtProvider;
    private final RedisEmailAuthentication redisEmailAuthentication;

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

    @Override
    public LoginResponseDTO loginUser(LoginRequestDTO requestDTO) {
        User user = userRepository.findByEmail(requestDTO.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        if (bCryptPasswordEncoder.matches(requestDTO.getPassword(), user.getPassword())) {
            String token = jwtProvider.generateJwtToken(user.getUserId(), 3600000);

            LoginResponseDTO userDto = new LoginResponseDTO();
            userDto.setEmail(user.getEmail());
            userDto.setToken(token);

            return userDto;
        } else {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    @Transactional
    @Override
    public void sendAuthenticationCode(String email) throws MessagingException, UnsupportedEncodingException {
        // 인증코드 생성
        String code = mailService.createRandomCode();
        // Redis 내부에 생성한 인증코드 저장 및 유효기간 5분으로 설정
        redisEmailAuthentication.setEmailAuthenticationExpire(email, code, 5L);

        String text = "";
        text += "안녕하세요.";
        text += "인증코드 보내드립니다.";
        text += "<br/><br/>";
        text += "인증코드 : <b>"+code+"</b>";

        EmailDTO data = EmailDTO.builder()
                .email(email)
                .title("이메일 인증코드 발송 메일입니다.")
                .text(text)
                .build();

        // 입력한 이메일로 인증코드 발송
        mailService.sendMail(data);
    }

    @Transactional
    @Override
    public void emailAuthentication(String email, String code) {
        // 회원 이메일로 전송된 인증코드
        String emailCode = redisEmailAuthentication.getEmailAuthenticationCode(email);

        // Redis 내부에 이메일이 존재하는지 확인
        if(code == null) {
            throw new IllegalArgumentException("등록되지 않은 이메일입니다.");
        }

        // 입력한 인증코드와 발송된 인증코드 값 비교
        if(!emailCode.equals(code)) {
            throw new IllegalArgumentException("이메일 인증코드가 일치하지 않습니다.");
        }

        // 이메일 인증 완료 처리
        redisEmailAuthentication.setEmailAuthenticationComplete(email);
    }
}
