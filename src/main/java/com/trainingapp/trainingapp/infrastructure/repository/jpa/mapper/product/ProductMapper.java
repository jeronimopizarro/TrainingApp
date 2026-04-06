package com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.product;

import com.trainingapp.trainingapp.domain.entity.product.Product;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.product.ProductJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toDomain(ProductJpaEntity entity) {
        if (entity == null) return null;

        return Product.restore(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getStock(),
                entity.getImageUrl(),
                entity.getGymId(),
                entity.isActive()
        );
    }

    public ProductJpaEntity toEntity(Product domain) {
        if (domain == null) return null;

        ProductJpaEntity entity = new ProductJpaEntity();

        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setPrice(domain.getPrice());
        entity.setStock(domain.getStock());
        entity.setImageUrl(domain.getImageUrl());
        entity.setActive(domain.isActive());
        entity.setGymId(domain.getGymId());

        return entity;
    }
}