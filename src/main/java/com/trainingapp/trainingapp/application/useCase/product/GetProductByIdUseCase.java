package com.trainingapp.trainingapp.application.useCase.product;

import com.trainingapp.trainingapp.application.mapper.product.ProductDTOMapper;
import com.trainingapp.trainingapp.domain.entity.product.Product;
import com.trainingapp.trainingapp.domain.exception.product.ProductNotFoundException;
import com.trainingapp.trainingapp.domain.repository.product.ProductRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.product.ProductResponse;
import org.springframework.stereotype.Service;

@Service
public class GetProductByIdUseCase {

    private final ProductRepository productRepository;
    private final ProductDTOMapper productDTOMapper;
    private final SecurityUtils securityUtils;

    public GetProductByIdUseCase(ProductRepository productRepository,
                                 ProductDTOMapper productDTOMapper,
                                 SecurityUtils securityUtils) {
        this.productRepository = productRepository;
        this.productDTOMapper = productDTOMapper;
        this.securityUtils = securityUtils;
    }

    public ProductResponse execute(Long id) {
        Product product = findProductOrThrow(id);

        securityUtils.validateSameGym(product.getGymId());

        return productDTOMapper.toResponse(product);
    }

    private Product findProductOrThrow(Long id) {
        return productRepository.findById(id).orElseThrow(
                () -> new ProductNotFoundException(id));
    }
}
