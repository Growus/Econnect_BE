package com.growus.econnect.dto.user;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordRequestDTO {
    private String currentPassword;
    private String newPassword;
}
