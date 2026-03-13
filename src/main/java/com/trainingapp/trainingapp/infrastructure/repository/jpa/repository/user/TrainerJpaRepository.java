package com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.user;

import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.user.TrainerJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TrainerJpaRepository extends JpaRepository<TrainerJpaEntity, Long> {

    List<TrainerJpaEntity> findByGymIdAndActiveTrue(Long gymId);
}