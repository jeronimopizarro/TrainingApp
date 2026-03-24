package com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.product;

import com.trainingapp.trainingapp.domain.entity.product.Product;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.product.ProductJpaEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ProductMapper {

    public Product toDomain(ProductJpaEntity entity) {
        return new Product(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getStock(),
                entity.getImageUrl(),
                entity.isActive(),
                entity.getGymId()
        );
    }

    public ProductJpaEntity toEntity(Product domain) {
        ProductJpaEntity entity = new ProductJpaEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setPrice(domain.getPrice());
        entity.setStock(domain.getStock());
        entity.setImageUrl(domain.getImageUrl());
        entity.setActive(domain.isActive());
        entity.setGymId(domain.getGymId());

        if (!domain.isActive() && entity.getDeletedAt() == null) {
            entity.setDeletedAt(LocalDateTime.now());
        } else if (domain.isActive()) {
            entity.setDeletedAt(null);
        }

        return entity;
    }
}