package com.trainingapp.trainingapp.application.useCase.product;

import com.trainingapp.trainingapp.application.mapper.product.ProductDTOMapper;
import com.trainingapp.trainingapp.application.validator.GymValidator;
import com.trainingapp.trainingapp.domain.entity.product.Product;
import com.trainingapp.trainingapp.domain.repository.product.ProductRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.product.CreateProductRequest;
import com.trainingapp.trainingapp.web.dto.product.ProductResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateProductUseCase {

    private final ProductRepository productRepository;
    private final ProductDTOMapper productDTOMapper;
    private final GymValidator gymValidator;
    private final SecurityUtils securityUtils;

    public CreateProductUseCase(ProductRepository productRepository,
                                ProductDTOMapper productDTOMapper,
                                GymValidator gymValidator, SecurityUtils securityUtils) {
        this.productRepository = productRepository;
        this.productDTOMapper = productDTOMapper;
        this.gymValidator = gymValidator;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public ProductResponse execute(CreateProductRequest request) {
        gymValidator.validateExists(request.gymId());
        securityUtils.validateSameGym(request.gymId());

        Product product = productDTOMapper.toDomain(request);

        Product savedProduct = productRepository.save(product);
        return productDTOMapper.toResponse(savedProduct);
    }
}