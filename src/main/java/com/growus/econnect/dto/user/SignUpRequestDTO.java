package com.growus.econnect.dto.user;

import lombok.Data;

@Data
public class SignUpRequestDTO {
    private String nickName;
    private String email;
    private String password;
}
