package com.trainingapp.trainingapp.application.useCase.product;

import com.trainingapp.trainingapp.application.mapper.product.ProductDTOMapper;
import com.trainingapp.trainingapp.application.validator.GymValidator;
import com.trainingapp.trainingapp.domain.entity.product.Product;
import com.trainingapp.trainingapp.domain.repository.product.ProductRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.product.ProductResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchProductsByNameUseCase {

    private final ProductRepository productRepository;
    private final ProductDTOMapper productDTOMapper;
    private final GymValidator gymValidator;
    private final SecurityUtils securityUtils;

    public SearchProductsByNameUseCase(ProductRepository productRepository, ProductDTOMapper productDTOMapper, GymValidator gymValidator,
                                       SecurityUtils securityUtils) {
        this.productRepository = productRepository;
        this.productDTOMapper = productDTOMapper;
        this.gymValidator = gymValidator;
        this.securityUtils = securityUtils;
    }

    public List<ProductResponse> execute(Long gymId, String name) {
        gymValidator.validateExists(gymId);
        securityUtils.validateSameGym(gymId);

        List<Product> products = findProducts(gymId, name);

        return products.stream()
                .map(productDTOMapper::toResponse)
                .toList();
    }

    private List<Product> findProducts(Long gymId, String name) {
        if (isSearchQueryEmpty(name)) {
            return productRepository.findAllByGymId(gymId);
        }
        return productRepository.searchByName(gymId, name);
    }

    private boolean isSearchQueryEmpty(String name) {
        return name == null || name.trim().isEmpty();
    }
}