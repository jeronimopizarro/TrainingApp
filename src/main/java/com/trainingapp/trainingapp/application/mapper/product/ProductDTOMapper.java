package com.trainingapp.trainingapp.application.mapper.product;

import com.trainingapp.trainingapp.domain.entity.product.Product;
import com.trainingapp.trainingapp.web.dto.product.CreateProductRequest;
import com.trainingapp.trainingapp.web.dto.product.ProductResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductDTOMapper {

    public Product toDomain(CreateProductRequest request) {
        if (request == null) return null;

        return Product.createNew(
                request.name(),
                request.description(),
                request.price(),
                request.stock(),
                request.imageUrl(),
                request.gymId()
        );
    }

    public ProductResponse toResponse(Product product) {
        if (product == null) return null;

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getImageUrl(),
                product.isActive(),
                product.getGymId()
        );
    }
}