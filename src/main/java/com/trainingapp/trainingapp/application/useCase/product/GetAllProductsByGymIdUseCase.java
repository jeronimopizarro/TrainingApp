package com.trainingapp.trainingapp.application.useCase.product;

import com.trainingapp.trainingapp.application.mapper.product.ProductDTOMapper;
import com.trainingapp.trainingapp.application.validator.GymValidator;
import com.trainingapp.trainingapp.domain.entity.membership.MembershipPlan;
import com.trainingapp.trainingapp.domain.entity.product.Product;
import com.trainingapp.trainingapp.domain.repository.product.ProductRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.product.ProductResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllProductsByGymIdUseCase {
    private final ProductRepository productRepository;
    private final ProductDTOMapper productDTOMapper;
    private final GymValidator gymValidator;
    private final SecurityUtils securityUtils;

    public GetAllProductsByGymIdUseCase(ProductRepository productRepository,
                                        ProductDTOMapper productDTOMapper,
                                        GymValidator gymValidator,
                                        SecurityUtils securityUtils) {
        this.productRepository = productRepository;
        this.productDTOMapper = productDTOMapper;
        this.gymValidator = gymValidator;
        this.securityUtils = securityUtils;
    }

    public List<ProductResponse> execute(Long gymId, String stockStatus) {
        gymValidator.validateExists(gymId);
        securityUtils.validateSameGym(gymId);

        List<Product> products;
        
        if ("LOW_STOCK".equalsIgnoreCase(stockStatus)) {
            products = productRepository.findByStockRange(gymId, 1, 5);
        } else if ("OUT_OF_STOCK".equalsIgnoreCase(stockStatus)) {
            products = productRepository.findWithNoStock(gymId);
        } else {
            products = productRepository.findAllByGymId(gymId);
        }

        return products.stream().map(productDTOMapper::toResponse).toList();
    }
}