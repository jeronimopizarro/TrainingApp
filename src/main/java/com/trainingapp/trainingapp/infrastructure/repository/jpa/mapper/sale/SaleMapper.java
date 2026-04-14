package com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.sale;

import com.trainingapp.trainingapp.domain.entity.sale.Sale;
import com.trainingapp.trainingapp.domain.entity.sale.SaleDetail;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.sale.SaleDetailJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.sale.SaleJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SaleMapper {

    public Sale toDomain(SaleJpaEntity entity) {
        if (entity == null) return null;

        List<SaleDetail> domainDetails = mapDetailsToDomain(entity.getDetails());

        return Sale.restore(
                entity.getId(),
                entity.getSaleDate(),
                entity.getTotalAmount(),
                entity.getPaymentMethod(),
                entity.getGymId(),
                entity.getRegisteredByAdminId(),
                domainDetails
        );
    }

    private List<SaleDetail> mapDetailsToDomain(List<SaleDetailJpaEntity> detailEntities) {
        if (detailEntities == null) return new java.util.ArrayList<>();

        return detailEntities.stream()
                .map(this::toDetailDomain)
                .toList();
    }

    private SaleDetail toDetailDomain(SaleDetailJpaEntity detailEntity) {
        return SaleDetail.restore(
                detailEntity.getId(),
                detailEntity.getProductId(),
                detailEntity.getQuantity(),
                detailEntity.getUnitPrice()
        );
    }

    // --- DE DOMINIO A JPA ---
    public SaleJpaEntity toEntity(Sale domain) {
        SaleJpaEntity entity = new SaleJpaEntity();
        entity.setId(domain.getId());
        entity.setSaleDate(domain.getSaleDate());
        entity.setTotalAmount(domain.getTotalAmount());
        entity.setPaymentMethod(domain.getPaymentMethod());
        entity.setGymId(domain.getGymId());
        entity.setRegisteredByAdminId(domain.getRegisteredByAdminId());

        // Usamos el Helper Method para mantener la bidireccionalidad segura
        domain.getDetails().forEach(detail -> entity.addDetail(toDetailEntity(detail)));

        return entity;
    }

    private SaleDetailJpaEntity toDetailEntity(SaleDetail detail) {
        SaleDetailJpaEntity entity = new SaleDetailJpaEntity();
        entity.setId(detail.getId());
        entity.setProductId(detail.getProductId());
        entity.setQuantity(detail.getQuantity());
        entity.setUnitPrice(detail.getUnitPrice());
        return entity;
    }
}
