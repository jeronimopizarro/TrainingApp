package com.trainingapp.trainingapp.infrastructure.repository.jpa.repository;

import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.RoutineJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoutineJpaRepository extends JpaRepository<RoutineJpaEntity, Long> {
    List<RoutineJpaEntity> findAllByMemberId(Long memberId);
}