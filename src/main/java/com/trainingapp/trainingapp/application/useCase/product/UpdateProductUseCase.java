package com.trainingapp.trainingapp.application.useCase.product;

import com.trainingapp.trainingapp.application.mapper.product.ProductDTOMapper;
import com.trainingapp.trainingapp.domain.entity.product.Product;
import com.trainingapp.trainingapp.domain.exception.product.ProductNotFoundException;
import com.trainingapp.trainingapp.domain.repository.product.ProductRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.product.CreateProductRequest;
import com.trainingapp.trainingapp.web.dto.product.ProductResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UpdateProductUseCase {

    private final ProductRepository productRepository;
    private final ProductDTOMapper productDTOMapper;
    private final SecurityUtils securityUtils;

    public UpdateProductUseCase(ProductRepository productRepository, 
                                ProductDTOMapper productDTOMapper,
                                SecurityUtils securityUtils) {
        this.productRepository = productRepository;
        this.productDTOMapper = productDTOMapper;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public ProductResponse execute(Long id, CreateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        securityUtils.validateSameGym(product.getGymId());

        product.updateDetails(request.name(), request.description(), request.imageUrl());
        product.updatePrice(request.price());
        
        // El stock se maneja usualmente por separado, pero para el CRUD básico:
        if (request.stock() != null && request.stock() >= 0) {
            int currentStock = product.getStock();
            if (request.stock() > currentStock) {
                product.addStock(request.stock() - currentStock);
            } else if (request.stock() < currentStock) {
                product.reduceStock(currentStock - request.stock());
            }
        }

        Product updatedProduct = productRepository.save(product);
        return productDTOMapper.toResponse(updatedProduct);
    }
}