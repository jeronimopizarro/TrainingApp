package com.trainingapp.trainingapp.application.mapper.sale;

import com.trainingapp.trainingapp.domain.entity.product.Product;
import com.trainingapp.trainingapp.domain.entity.sale.Sale;
import com.trainingapp.trainingapp.domain.entity.sale.SaleDetail;
import com.trainingapp.trainingapp.domain.exception.product.ProductNotFoundException;
import com.trainingapp.trainingapp.domain.repository.product.ProductRepository;
import com.trainingapp.trainingapp.web.dto.sale.SaleDetailResponse;
import com.trainingapp.trainingapp.web.dto.sale.SaleResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SaleDTOMapper {

    private final ProductRepository productRepository;

    public SaleDTOMapper(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public SaleResponse toResponse(Sale sale) {
        List<SaleDetailResponse> detailResponses = sale.getDetails().stream()
                .map(this::toDetailResponse)
                .toList();

        return new SaleResponse(
                sale.getId(),
                sale.getSaleDate(),
                sale.getTotalAmount(),
                sale.getPaymentMethod(),
                sale.getGymId(),
                sale.getRegisteredByAdminId(),
                sale.getMemberId(),
                detailResponses
        );
    }

    private SaleDetailResponse toDetailResponse(SaleDetail detail) {
        Product product = productRepository.findById(detail.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado en el catálogo."));

        return new SaleDetailResponse(
                detail.getId(),
                detail.getProductId(),
                product.getName(),
                detail.getQuantity(),
                detail.getUnitPrice(),
                detail.getSubtotal()
        );
    }
}