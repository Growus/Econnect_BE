package com.growus.econnect.base.apiResponse.code.status;

import com.growus.econnect.base.apiResponse.code.BaseCode;
import com.growus.econnect.base.apiResponse.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {

    // 일반적인 응답
    _OK(HttpStatus.OK, "COMMON200", "성공입니다."),

    // 유저 관련 응답
    SUCCESS_SIGNUP(HttpStatus.OK, "USER2000","회원가입을 성공했습니다."),
    SUCCESS_LOGIN(HttpStatus.OK, "USER2001","로그인을 성공했습니다."),
    SUCCESS_SEND_EMAIL(HttpStatus.OK, "USER2002","인증 번호 전송에 성공했습니다."),
    SUCCESS_CHECK_CODE(HttpStatus.OK, "USER2003","인증 번호 확인에 성공했습니다."),
    SUCCESS_GET_MYPAGE(HttpStatus.OK, "MYPAGE2000","마이페이지 조회에 성공했습니다."),
    SUCCESS_UPDATE_USER_INFO(HttpStatus.OK, "MYPAGE2001","회원 정보 수정에 성공했습니다."),
    SUCCESS_UPDATE_PASSWORD(HttpStatus.OK, "MYPAGE2002","비밀번호 변경에 성공했습니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReason() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .build();
    }

    @Override
    public ReasonDTO getReasonHttpStatus() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}
