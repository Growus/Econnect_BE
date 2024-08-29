package com.growus.econnect.dto.plant;

import com.growus.econnect.entity.Plant;
import com.growus.econnect.entity.PlantStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleResponseDTO {
    private int statusCode;
    private String message;
    private Object data;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlantDetailsDTO {
        private Long id;
        private String name;
        private String type;
        private LocalDateTime dDay;
        private String image;
        private boolean representative;
        private float solidHumidity;
        private float airHumidity;
        private float temperature;
        private PlantStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Long daysLeft;

        public PlantDetailsDTO(Plant plant) {
            this.id = plant.getId();
            this.name = plant.getName();
            this.type = plant.getType();
            this.dDay = plant.getDDay();
            this.image = plant.getImage();
            this.representative = plant.isRepresentative();
            this.solidHumidity = plant.getSolidHumidity();
            this.airHumidity = plant.getAirHumidity();
            this.temperature = plant.getTemperature();
            this.status = plant.getStatus();
            this.createdAt = plant.getCreatedAt();
            this.updatedAt = plant.getUpdatedAt();
            this.daysLeft = calculateDaysLeft(plant.getDDay());
        }

        private long calculateDaysLeft(LocalDateTime dDay) {
            if (dDay == null) {
                return 0;
            }
            return Math.abs(ChronoUnit.DAYS.between(LocalDateTime.now(), dDay));
        }
    }
}
