package com.trainingapp.trainingapp.application.useCase.product;

import com.trainingapp.trainingapp.domain.entity.product.Product;
import com.trainingapp.trainingapp.domain.exception.product.ProductNotFoundException;
import com.trainingapp.trainingapp.domain.repository.product.ProductRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteProductUseCase {
    private final ProductRepository productRepository;
    private final SecurityUtils securityUtils;

    public DeleteProductUseCase(ProductRepository productRepository, SecurityUtils securityUtils) {
        this.productRepository = productRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public void execute(Long id) {
        Product product = findProductOrThrow(id);
        securityUtils.validateSameGym(product.getGymId());

        product.deactivate();
        productRepository.save(product);
    }

    private Product findProductOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(
                        "El producto con ID " + id + " no existe."));
    }
}