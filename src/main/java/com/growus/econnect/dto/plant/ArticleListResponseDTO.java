package com.growus.econnect.dto.plant;

import com.growus.econnect.entity.Plant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleListResponseDTO {
    private int statusCode;
    private String message;
    private List<PlantSummaryDTO> plants;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlantSummaryDTO {
        private Long id;
        private String name;
        private String type;
        private String image;
        private boolean representative;
        private LocalDateTime dDay;

        public PlantSummaryDTO(Plant plant) {
            this.id = plant.getId();
            this.name = plant.getName();
            this.type = plant.getType();
            this.image = plant.getImage();
            this.representative = plant.isRepresentative();
            this.dDay = plant.getDDay();
        }
    }
}
