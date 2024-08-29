package com.growus.econnect.dto.plant;

import com.growus.econnect.entity.PlantStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddArticleRequestDTO {
    private Long userId;
    private String name;
    private String type;
    private LocalDateTime dDay;
    private MultipartFile imageFile;
    private Boolean representative;
    private Float solidHumidity;
    private Float airHumidity;
    private Float temperature;
    private PlantStatus status;
}
