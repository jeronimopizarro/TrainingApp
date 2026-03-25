package com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.sale;

import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.sale.SaleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaleJpaRepository extends JpaRepository<SaleJpaEntity, Long> {
    List<SaleJpaEntity> findAllByGymIdOrderBySaleDateDesc(Long gymId);
}