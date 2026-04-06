package com.trainingapp.trainingapp.domain.entity.sale;

import com.trainingapp.trainingapp.domain.exception.sale.InvalidSaleDetailException;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class SaleDetail {

    private final Long id;
    private Long productId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    public SaleDetail(Long id, Long productId, Integer quantity, BigDecimal unitPrice) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        validate();
        // Calculamos el subtotal una vez nació la entidad.
        this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    private void validate() {
        if (this.productId == null) {
            throw new InvalidSaleDetailException("El ID del producto es obligatorio.");
        }
        if (this.quantity == null || this.quantity <= 0) {
            throw new InvalidSaleDetailException("La cantidad debe ser mayor a cero.");
        }
        if (this.unitPrice == null || this.unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidSaleDetailException("El precio unitario no puede ser negativo.");
        }
    }

    public static SaleDetail createNew(Long productId, Integer quantity, BigDecimal unitPrice) {
        return new SaleDetail(null, productId, quantity, unitPrice);
    }

    public static SaleDetail restore(Long id, Long productId, Integer quantity, BigDecimal unitPrice) {
        return new SaleDetail(id, productId, quantity, unitPrice);
    }
}