package com.growus.econnect.controller;

import com.growus.econnect.dto.plant.MainResponseDTO;
import com.growus.econnect.dto.plant.MainResponseDTO.PlantSummaryDTO;
import com.growus.econnect.dto.user.MyPageResponseDTO;
import com.growus.econnect.service.user.MyPageService;
import com.growus.econnect.service.plant.PlantServiceImpl;
import com.growus.econnect.entity.Plant;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.growus.econnect.base.common.UserAuthorizationUtil.getCurrentUserId;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MainPageController {

    private final PlantServiceImpl plantService;
    private final MyPageService myPageService;

    @GetMapping("/main-page")
    public MainResponseDTO getMainPage() {
        // 사용자 정보 가져오기
        MyPageResponseDTO userResponse = myPageService.getUser();

        String nickname = userResponse.getNickname();
        String profileImage = userResponse.getProfileImage();

        // 현재 사용자의 식물 목록 가져오기
        Long userId = getCurrentUserId(); // 현재 인증된 사용자 ID 가져오기
        List<Plant> plants = plantService.getPlantsByUserId(userId);

        // 식물 정보를 DTO로 변환하면서 daysLeft 계산
        List<PlantSummaryDTO> plantSummaries = plants.stream()
                .map(plant -> new PlantSummaryDTO(
                        plant.getName(),
                        plant.getImage(),
                        plant.isRepresentative(),
                        calculateDaysLeft(plant.getDDay())  // Ensure the correct getter method is used
                ))
                .collect(Collectors.toList());

        // MainResponseDTO 생성
        return new MainResponseDTO(nickname, profileImage, plantSummaries);
    }

    // daysLeft를 계산하는 메소드
    private long calculateDaysLeft(LocalDateTime dDay) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return Math.abs(ChronoUnit.DAYS.between(currentDateTime.toLocalDate(), dDay.toLocalDate()));
    }
}
