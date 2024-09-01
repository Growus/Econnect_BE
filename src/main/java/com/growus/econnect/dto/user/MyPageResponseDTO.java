package com.growus.econnect.dto.user;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyPageResponseDTO {
    private String profileImage;
    private String nickname;
    private String stateMessage;
}
