package com.trainingapp.trainingapp.domain.entity.sale;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class SaleDetail {

    private Long id;
    private Long productId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    public SaleDetail(Long id, Long productId, Integer quantity, BigDecimal unitPrice) {
        validateFields(productId, quantity, unitPrice);

        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public static SaleDetail createNew(Long productId, Integer quantity, BigDecimal unitPrice) {
        return new SaleDetail(null, productId, quantity, unitPrice);
    }

    private void validateFields(Long productId, Integer quantity, BigDecimal unitPrice) {
        if (productId == null) {
            throw new IllegalArgumentException("El ID del producto es obligatorio.");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser negativo.");
        }
    }
}