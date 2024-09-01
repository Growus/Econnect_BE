package com.growus.econnect.base.apiResponse.exception;

import lombok.Getter;

@Getter
public class InvalidPasswordException extends RuntimeException {

    private final String errorCode; // 오류 코드를 저장할 변수

    // 생성자에서 오류 코드와 메시지를 설정
    public InvalidPasswordException(String errorCode) {
        super("비밀번호 오류 발생: " + errorCode);
        this.errorCode = errorCode;
    }
}
