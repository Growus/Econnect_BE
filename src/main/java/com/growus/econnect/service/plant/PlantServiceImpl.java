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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlantServiceImpl implements PlantService {

    private final PlantRepository plantRepository;
    private final UserRepository userRepository;
    private final PlantTypeService plantTypeService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${openapi.api.key}")
    private String apiKey;

    @Value("${openapi.api.url}")
    private String apiUrl;


    // 식물 등록
    @Transactional
    @Override
    public ArticleResponseDTO createPlant(AddArticleRequestDTO addArticleRequestDTO) {
        if (addArticleRequestDTO.getUserId() == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }

        User user = userRepository.findById(addArticleRequestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String defaultImagePath = "C:\\econnectProject\\uploads\\식물.png";
        String imagePath = defaultImagePath;

        if (addArticleRequestDTO.getImageFile() != null && !addArticleRequestDTO.getImageFile().isEmpty()) {
            imagePath = storeFile(addArticleRequestDTO.getImageFile());
        }

        // Fetching cntntsNo based on the selected type
        String cntntsNo = addArticleRequestDTO.getCntntsNo();

        Plant plant = Plant.builder()
                .user(user)
                .name(addArticleRequestDTO.getName())
                .type(addArticleRequestDTO.getType())
                .cntntsNo(addArticleRequestDTO.getCntntsNo() != null ? addArticleRequestDTO.getCntntsNo() : "zero")
                .dDay(addArticleRequestDTO.getDDay())
                .image(imagePath)
                .representative(addArticleRequestDTO.getRepresentative() != null ? addArticleRequestDTO.getRepresentative() : false)
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
                "Plant created successfully",
                new ArticleResponseDTO.PlantDetailsDTO(savedPlant)
        );
    }

    // 이미지 저장
    @Override
    public String storeFile(MultipartFile file) {
        try {
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File destinationFile = new File(directory, file.getOriginalFilename());
            file.transferTo(destinationFile);

            return destinationFile.getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException("파일을 저장할 수 없습니다. 오류: " + file.getOriginalFilename(), e);
        }
    }

    // 식물 상세 조회
    @Override
    public Optional<Plant> getPlantById(Long id) {
        return plantRepository.findById(id);
    }

    // 식물 상세 조회
    public Optional<Plant> getPlantByIdAndUserId(Long id, Long userId) {
        // 데이터베이스에서 식물 조회
        Optional<Plant> plantOptional = plantRepository.findByIdAndUser_UserId(id, userId);

        if (plantOptional.isPresent()) {
            Plant plant = plantOptional.get();
            // cntntsNo를 사용하여 추가 정보 가져오기
            PlantTypeDTO plantTypeDTO = plantTypeService.getPlantTypeByCntntsNo(plant.getCntntsNo());

            if (plantTypeDTO != null) {
                plant.setSpeclmanageInfo(plantTypeDTO.getSpeclmanageInfo());
            }
        }

        return plantOptional;
    }
    // 식물 목록 조회
    @Override
    public List<Plant> getPlantsByUserId(Long userId) {
        return plantRepository.findByUser_UserId(userId);
    }

    // 식물 삭제
    @Transactional
    @Override
    public void deletePlant(Long id, Long userId) {
        Plant plant = plantRepository.findByIdAndUser_UserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Plant not found or unauthorized"));

        plantRepository.delete(plant);
    }

    // 식물 수정
    @Transactional
    @Override
    public ArticleResponseDTO updatePlant(Long id, Long userId, UpdateArticleRequestDTO updateArticleRequestDTO) {
        Plant plant = plantRepository.findByIdAndUser_UserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Plant not found or unauthorized"));

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
            String imagePath = storeFile(updateArticleRequestDTO.getImageFile());
            plant.setImage(imagePath);
        }

        plant.setUpdatedAt(LocalDateTime.now());

        Plant updatedPlant = plantRepository.save(plant);

        return new ArticleResponseDTO(
                HttpStatus.OK.value(),
                "Plant updated successfully",
                new ArticleResponseDTO.PlantDetailsDTO(updatedPlant)
        );
    }
    // 대표 식물 설정
    @Transactional
    @Override
    public void setRepresentative(Long plantId, Long userId) {
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
                .orElseThrow(() -> new RuntimeException("Plant not found or unauthorized"));

        newRepresentative.setRepresentative(true);
        plantRepository.save(newRepresentative);
    }

}
