package com.growus.econnect.base.apiResponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.growus.econnect.base.apiResponse.code.BaseCode;
import com.growus.econnect.base.apiResponse.code.BaseErrorCode;
import com.growus.econnect.base.apiResponse.code.status.SuccessStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class ApiResponse<T> {

    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
    private final String code;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;


    // 성공한 경우 응답 생성
    public static <T> ApiResponse<T> onSuccess(BaseCode code, T data){
        return new ApiResponse<>(true, code.getReasonHttpStatus().getCode() , code.getReasonHttpStatus().getMessage(), data);
    }

    // 실패한 경우 응답 생성
    public static <T> ApiResponse<T> onFailure(BaseErrorCode code, T data){
        return new ApiResponse<>(false,code.getReasonHttpStatus().getCode() , code.getReasonHttpStatus().getMessage(), data);
    }
}
