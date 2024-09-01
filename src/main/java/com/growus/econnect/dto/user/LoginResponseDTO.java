package com.growus.econnect.dto.user;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private String email;
    private String token;
}
