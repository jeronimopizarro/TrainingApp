package com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.gym;

import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.gym.GymJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GymJpaRespository extends JpaRepository<GymJpaEntity, Long> {
}