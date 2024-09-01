package com.growus.econnect.service.plant;

import com.growus.econnect.dto.plant.AddArticleRequestDTO;
import com.growus.econnect.dto.plant.ArticleResponseDTO;
import com.growus.econnect.dto.plant.UpdateArticleRequestDTO;
import com.growus.econnect.entity.Plant;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface PlantService {
    ArticleResponseDTO createPlant(AddArticleRequestDTO addArticleRequestDTO);
    Optional<Plant> getPlantById(Long id);
    Optional<Plant> getPlantByIdAndUserId(Long id, Long userId);
    List<Plant> getPlantsByUserId(Long userId);
    String storeFile(MultipartFile file);
    void deletePlant(Long id, Long userId);
    ArticleResponseDTO updatePlant(Long id, Long userId, UpdateArticleRequestDTO updateArticleRequestDTO);
    void setRepresentative(Long plantId, Long userId);
}
