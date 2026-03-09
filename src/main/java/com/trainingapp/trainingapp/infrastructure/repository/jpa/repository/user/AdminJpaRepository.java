package com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.user;

import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.user.AdminJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AdminJpaRepository extends JpaRepository<AdminJpaEntity, Long> {

    List<AdminJpaEntity> findByGymId(Long gymId);
}