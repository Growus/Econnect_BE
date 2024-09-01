package com.growus.econnect.controller;

import com.growus.econnect.dto.plant.*;
import com.growus.econnect.entity.Plant;
import com.growus.econnect.entity.PlantStatus;
import com.growus.econnect.service.plant.PlantService;
import com.growus.econnect.service.plant.PlantTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/plants")
@RequiredArgsConstructor
public class PlantController {

    private final PlantService plantService;
    private final PlantTypeService plantTypeService;


    // 식물 등록 엔드포인트
    @PostMapping(value = "/articles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ArticleResponseDTO> createPlant(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("userId") Long userId,
            @RequestParam("name") String name,
            @RequestParam("type") String type,
            @RequestParam("dDay") LocalDate dDay,
            @RequestParam(value = "representative", required = false) Boolean representative, // Boolean으로 변경
            @RequestParam(value = "solidHumidity", required = false) Float solidHumidity,
            @RequestParam(value = "airHumidity", required = false) Float airHumidity,
            @RequestParam(value = "temperature", required = false) Float temperature,
            @RequestParam(value = "status", required = false) PlantStatus status,
            @RequestParam(value = "cntntsNo", required = false) String cntntsNo) { // cntntsNo 추가

        // LocalDate를 LocalDateTime으로 변환
        LocalDateTime dDayDateTime = dDay.atStartOfDay();

        AddArticleRequestDTO addArticleRequestDTO = new AddArticleRequestDTO(
                userId, name, type, dDayDateTime, file, representative, solidHumidity, airHumidity, temperature, status, cntntsNo);
        ArticleResponseDTO responseDTO = plantService.createPlant(addArticleRequestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    // 사용자 식물 상세 수정 엔드포인트
    @PutMapping(value = "/{userId}/articles/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ArticleResponseDTO> updatePlant(
            @PathVariable Long userId,
            @PathVariable Long id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "dDay", required = false) LocalDate dDay) {

        Optional<Plant> existingPlantOpt = plantService.getPlantByIdAndUserId(id, userId);

        if (existingPlantOpt.isEmpty()) {
            return new ResponseEntity<>(new ArticleResponseDTO(
                    HttpStatus.NOT_FOUND.value(),
                    "Plant not found or unauthorized",
                    null), HttpStatus.NOT_FOUND);
        }

        LocalDateTime dDayDateTime = dDay != null ? dDay.atStartOfDay() : null;

        UpdateArticleRequestDTO updateArticleRequestDTO = new UpdateArticleRequestDTO(name, type, dDayDateTime, file);

        ArticleResponseDTO responseDTO = plantService.updatePlant(id, userId, updateArticleRequestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    // 사용자 식물 목록 조회 엔드포인트
    @GetMapping("/{userId}/articles/")
    public ResponseEntity<ArticleListResponseDTO> getPlantsByUser(@PathVariable Long userId) {
        List<Plant> plants = plantService.getPlantsByUserId(userId);
        List<ArticleListResponseDTO.PlantSummaryDTO> plantSummaries = plants.stream()
                .map(ArticleListResponseDTO.PlantSummaryDTO::new)
                .collect(Collectors.toList());

        ArticleListResponseDTO responseDTO = new ArticleListResponseDTO(
                HttpStatus.OK.value(),
                "User's plants retrieved successfully",
                plantSummaries
        );
        return ResponseEntity.ok(responseDTO);
    }

    // 사용자 식물 상세 조회 엔드포인트
    @GetMapping("/{userId}/articles/{id}")
    public ResponseEntity<ArticleResponseDTO> getPlantById(@PathVariable Long userId, @PathVariable Long id) {
        Optional<Plant> plantOptional = plantService.getPlantByIdAndUserId(id, userId);

        if (plantOptional.isPresent()) {
            // 변환 작업: Plant 객체를 PlantDetailsDTO로 변환
            ArticleResponseDTO responseDTO = new ArticleResponseDTO(
                    HttpStatus.OK.value(),
                    "Plant found",
                    new ArticleResponseDTO.PlantDetailsDTO(plantOptional.get())
            );
            return ResponseEntity.ok(responseDTO);
        } else {
            ArticleResponseDTO responseDTO = new ArticleResponseDTO(
                    HttpStatus.NOT_FOUND.value(),
                    "Plant not found or unauthorized access",
                    null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
        }
    }

    // 사용자 식물 상세 삭제 엔드포인트
    @DeleteMapping("/{userId}/articles/{id}")
    public ResponseEntity<ArticleResponseDTO> deletePlant(@PathVariable Long userId, @PathVariable Long id) {
        try {
            plantService.deletePlant(id, userId);
            ArticleResponseDTO responseDTO = new ArticleResponseDTO(
                    HttpStatus.OK.value(),
                    "Plant deleted successfully",
                    null
            );
            return ResponseEntity.ok(responseDTO);
        } catch (RuntimeException e) {
            ArticleResponseDTO responseDTO = new ArticleResponseDTO(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
        }
    }

    // 식물 타입 리스트를 반환하는 엔드포인트
    @GetMapping("/types")
    public ResponseEntity<List<PlantTypeDTO>> getPlantTypes(
            @RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(name = "numOfRows", defaultValue = "10") int numOfRows) {
        List<PlantTypeDTO> plantTypes = plantTypeService.getPlantTypes(pageNo, numOfRows);
        return ResponseEntity.ok(plantTypes);
    }

    @GetMapping("/types/{cntntsNo}")
    public ResponseEntity<PlantTypeDTO> getPlantTypeByCntntsNo(@PathVariable String cntntsNo) {
        PlantTypeDTO plantType = plantTypeService.getPlantTypeByCntntsNo(cntntsNo);
        if (plantType != null) {
            return ResponseEntity.ok(plantType);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // 대표 식물 설정 엔드포인트
    @PutMapping("/{userId}/articles/{id}/representative")
    public ResponseEntity<ArticleResponseDTO> setRepresentative(
            @PathVariable Long userId,
            @PathVariable Long id) {
        try {
            plantService.setRepresentative(id, userId);
            ArticleResponseDTO responseDTO = new ArticleResponseDTO(
                    HttpStatus.OK.value(),
                    "Plant marked as representative successfully",
                    null
            );
            return ResponseEntity.ok(responseDTO);
        } catch (RuntimeException e) {
            ArticleResponseDTO responseDTO = new ArticleResponseDTO(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
        }
    }


}
