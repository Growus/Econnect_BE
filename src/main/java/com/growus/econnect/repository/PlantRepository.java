package com.growus.econnect.repository;

import com.growus.econnect.entity.Plant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlantRepository extends JpaRepository<Plant, Long> {
    Optional<Plant> findByIdAndUser_UserId(Long id, Long userId);
    List<Plant> findByUser_UserId(Long userId);
}
