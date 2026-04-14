package com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.product;

import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.product.ProductJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, Long> {

    List<ProductJpaEntity> findAllByGymIdAndActiveTrue(Long gymId);

    List<ProductJpaEntity> findByGymIdAndActiveTrueAndStockBetween(Long gymId, int min, int max);

    List<ProductJpaEntity> findByGymIdAndActiveTrueAndStockLessThanEqual(Long gymId, int max);

    List<ProductJpaEntity> findByGymIdAndNameContainingIgnoreCaseAndActiveTrue(Long gymId, String name);
}