package com.growus.econnect.dto.plant;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MainResponseDTO {
    private String nickname;
    private String profileImage;
    private List<PlantSummaryDTO> plants;

    @Data
    @AllArgsConstructor
    public static class PlantSummaryDTO {
        private String name;
        private String image;
        private boolean representative;
        private long daysLeft;
    }
}