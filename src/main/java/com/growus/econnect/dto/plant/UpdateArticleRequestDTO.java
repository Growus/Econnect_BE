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
public class UpdateArticleRequestDTO {
    private String name;
    private String type;
    private LocalDateTime dDay;
    private MultipartFile imageFile;
}
