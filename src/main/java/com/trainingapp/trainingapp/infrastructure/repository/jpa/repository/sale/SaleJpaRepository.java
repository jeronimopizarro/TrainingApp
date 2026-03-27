package com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.sale;

import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.sale.SaleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SaleJpaRepository extends JpaRepository<SaleJpaEntity, Long> {

    List<SaleJpaEntity> findAllByGymIdOrderBySaleDateDesc(Long gymId);

    @Query("SELECT new com.trainingapp.trainingapp.web.dto.dashboard.AdminDashboardResponse$TopProductDTO(sd.productId, p.name, SUM(sd.quantity)) " +
            "FROM SaleDetailJpaEntity sd " +
            "JOIN sd.sale s " +
            "JOIN ProductJpaEntity p ON sd.productId = p.id " +
            "WHERE s.gymId = :gymId " +
            "GROUP BY sd.productId, p.name " +
            "ORDER BY SUM(sd.quantity) DESC")
    List<com.trainingapp.trainingapp.web.dto.dashboard.AdminDashboardResponse.TopProductDTO> findTopProductsByGym(
            @Param("gymId") Long gymId,
            Pageable pageable);
}