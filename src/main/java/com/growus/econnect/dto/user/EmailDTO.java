package com.growus.econnect.dto.user;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailDTO {
    private String email;
    private String title;
    private String text;
}
