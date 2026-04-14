package com.trainingapp.trainingapp.application.useCase.sale;

import com.trainingapp.trainingapp.domain.exception.sale.SaleNotFoundException;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.sale.SaleJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.sale.SaleJpaRepository;
import com.trainingapp.trainingapp.web.dto.sale.SaleDetailResponse;
import com.trainingapp.trainingapp.web.dto.sale.SaleResponse;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.product.ProductJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class GetSaleByIdUseCase {

    private final SaleJpaRepository saleRepository;
    private final ProductJpaRepository productRepository;
    private final SecurityUtils securityUtils;

    public GetSaleByIdUseCase(SaleJpaRepository saleRepository,
                             ProductJpaRepository productRepository,
                             SecurityUtils securityUtils) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional(readOnly = true)
    public SaleResponse execute(Long id) {
        SaleJpaEntity sale = saleRepository.findById(id)
                .orElseThrow(() -> new SaleNotFoundException("Venta no encontrada con ID: " + id));

        securityUtils.validateSameGym(sale.getGymId());

        return new SaleResponse(
                sale.getId(),
                sale.getSaleDate(),
                sale.getTotalAmount(),
                sale.getPaymentMethod(),
                sale.getGymId(),
                sale.getRegisteredByAdminId(),
                sale.getMemberId(),
                sale.getDetails().stream()
                        .map(d -> {
                            String productName = productRepository.findById(d.getProductId())
                                    .map(p -> p.getName())
                                    .orElse("Producto Desconocido");
                            return new SaleDetailResponse(
                                    d.getId(),
                                    d.getProductId(),
                                    productName,
                                    d.getQuantity(),
                                    d.getUnitPrice(),
                                    d.getUnitPrice().multiply(java.math.BigDecimal.valueOf(d.getQuantity()))
                            );
                        })
                        .collect(Collectors.toList())
        );
    }
}
