package com.growus.econnect.service.plant;

import com.growus.econnect.dto.plant.AddArticleRequestDTO;
import com.growus.econnect.dto.plant.ArticleResponseDTO;
import com.growus.econnect.dto.plant.PlantTypeDTO;
import com.growus.econnect.dto.plant.UpdateArticleRequestDTO;
import com.growus.econnect.entity.Plant;
import com.growus.econnect.entity.PlantStatus;
import com.growus.econnect.entity.User;
import com.growus.econnect.repository.PlantRepository;
import com.growus.econnect.repository.UserRepository;
import com.growus.econnect.service.S3Uploader;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.growus.econnect.base.common.UserAuthorizationUtil.getCurrentUserId;

@Service
@RequiredArgsConstructor
public class PlantServiceImpl implements PlantService {

    private final PlantRepository plantRepository;
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${openapi.api.key}")
    private String apiKey;

    @Value("${openapi.api.url}")
    private String apiUrl;

    // 식물 등록
    @Transactional
    @Override
    public ArticleResponseDTO createPlant(Long userId, AddArticleRequestDTO addArticleRequestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        String defaultImagePath = "default_image_url"; // Use a placeholder URL if no image is provided
        String imagePath = defaultImagePath;

        if (addArticleRequestDTO.getImageFile() != null && !addArticleRequestDTO.getImageFile().isEmpty()) {
            try {
                imagePath = s3Uploader.upload(addArticleRequestDTO.getImageFile(), "plants");
            } catch (IOException e) {
                throw new RuntimeException("식물 이미지 업로드 중 오류가 발생했습니다.", e);
            }
        }

        boolean isFirstPlant = plantRepository.findByUser_UserId(userId).isEmpty();

        Plant plant = Plant.builder()
                .user(user)
                .name(addArticleRequestDTO.getName())
                .type(addArticleRequestDTO.getType())
                .cntntsNo(addArticleRequestDTO.getCntntsNo() != null ? addArticleRequestDTO.getCntntsNo() : "zero")
                .speclmanageInfo("")
                .dDay(addArticleRequestDTO.getDDay())
                .image(imagePath)
                .representative(isFirstPlant || (addArticleRequestDTO.getRepresentative() != null ? addArticleRequestDTO.getRepresentative() : false))
                .solidHumidity(addArticleRequestDTO.getSolidHumidity() != null ? addArticleRequestDTO.getSolidHumidity() : 0.0f)
                .airHumidity(addArticleRequestDTO.getAirHumidity() != null ? addArticleRequestDTO.getAirHumidity() : 0.0f)
                .temperature(addArticleRequestDTO.getTemperature() != null ? addArticleRequestDTO.getTemperature() : 0.0f)
                .status(addArticleRequestDTO.getStatus() != null ? addArticleRequestDTO.getStatus() : PlantStatus.HEALTHY)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Plant savedPlant = plantRepository.save(plant);

        return new ArticleResponseDTO(
                HttpStatus.CREATED.value(),
                "식물이 성공적으로 등록되었습니다.",
                new ArticleResponseDTO.PlantDetailsDTO(savedPlant)
        );
    }

    // 식물 수정
    @Transactional
    @Override
    public ArticleResponseDTO updatePlant(Long id, Long userId, UpdateArticleRequestDTO updateArticleRequestDTO) {
        Plant plant = plantRepository.findByIdAndUser_UserId(id, userId)
                .orElseThrow(() -> new RuntimeException("식물을 찾을 수 없거나 권한이 없습니다."));

        if (updateArticleRequestDTO.getName() != null) {
            plant.setName(updateArticleRequestDTO.getName());
        }
        if (updateArticleRequestDTO.getType() != null) {
            plant.setType(updateArticleRequestDTO.getType());
        }
        if (updateArticleRequestDTO.getDDay() != null) {
            plant.setDDay(updateArticleRequestDTO.getDDay());
        }
        if (updateArticleRequestDTO.getImageFile() != null && !updateArticleRequestDTO.getImageFile().isEmpty()) {
            try {
                // 기존 이미지 삭제
                String oldImagePath = plant.getImage();
                s3Uploader.deleteFile(oldImagePath);

                // 새 이미지 업로드
                String newImagePath = s3Uploader.upload(updateArticleRequestDTO.getImageFile(), "plants");
                plant.setImage(newImagePath);
            } catch (IOException e) {
                throw new RuntimeException("식물 이미지 업로드 중 오류가 발생했습니다.", e);
            }
        }

        plant.setUpdatedAt(LocalDateTime.now());

        Plant updatedPlant = plantRepository.save(plant);

        return new ArticleResponseDTO(
                HttpStatus.OK.value(),
                "식물이 성공적으로 수정되었습니다.",
                new ArticleResponseDTO.PlantDetailsDTO(updatedPlant)
        );
    }

    // 식물 삭제
    @Transactional
    @Override
    public void deletePlant(Long id, Long userId) {
        Long currentUserId = getCurrentUserId(); // 현재 인증된 사용자 ID 가져오기

        if (!currentUserId.equals(userId)) {
            throw new SecurityException("권한 오류: 사용자 ID 불일치.");
        }

        Plant plant = plantRepository.findByIdAndUser_UserId(id, userId)
                .orElseThrow(() -> new RuntimeException("식물을 찾을 수 없거나 권한이 없습니다."));

        plantRepository.delete(plant);
    }

    // 대표 식물 설정
    @Transactional
    @Override
    public void setRepresentative(Long plantId, Long userId) {
        Long currentUserId = getCurrentUserId(); // 현재 인증된 사용자 ID 가져오기

        if (!currentUserId.equals(userId)) {
            throw new SecurityException("권한 오류: 사용자 ID 불일치.");
        }

        // 1. 사용자에 해당하는 기존 대표 식물 조회
        List<Plant> existingRepresentatives = plantRepository.findByUser_UserId(userId).stream()
                .filter(Plant::isRepresentative) // 대표 식물 필터링
                .toList();

        // 2. 기존 대표 식물의 대표 상태를 false로 설정
        for (Plant representative : existingRepresentatives) {
            representative.setRepresentative(false);
            plantRepository.save(representative);
        }

        // 3. 새로운 대표 식물의 대표 상태를 true로 설정
        Plant newRepresentative = plantRepository.findByIdAndUser_UserId(plantId, userId)
                .orElseThrow(() -> new RuntimeException("식물을 찾을 수 없거나 권한이 없습니다."));

        newRepresentative.setRepresentative(true);
        plantRepository.save(newRepresentative);
    }


    // 식물 조회
    @Override
    public Optional<Plant> getPlantById(Long id) {
        return plantRepository.findById(id);
    }

    @Override
    public List<Plant> getPlantsByUserId(Long userId) {
        return plantRepository.findByUser_UserId(userId);
    }
}
