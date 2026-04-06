package com.trainingapp.trainingapp.domain.entity.sale;

import com.trainingapp.trainingapp.domain.enums.transaction.PaymentMethod;
import com.trainingapp.trainingapp.domain.exception.sale.InvalidSaleException;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Sale {

    private final Long id;
    private LocalDateTime saleDate;
    private BigDecimal totalAmount;
    private PaymentMethod paymentMethod;
    private Long gymId;
    private Long registeredByAdminId;
    private Long memberId;
    private List<SaleDetail> details;

    public Sale(Long id, LocalDateTime saleDate, BigDecimal totalAmount, PaymentMethod paymentMethod,
                Long gymId, Long registeredByAdminId, Long memberId, List<SaleDetail> details) {
        this.id = id;
        this.saleDate = saleDate;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.gymId = gymId;
        this.registeredByAdminId = registeredByAdminId;
        this.memberId = memberId;
        this.details = details != null ? new ArrayList<>(details) : new ArrayList<>();
        validate();
    }

    private void validate() {
        if (this.paymentMethod == null) {
            throw new InvalidSaleException("El método de pago es obligatorio.");
        }
        if (this.gymId == null) {
            throw new InvalidSaleException("La venta debe estar asociada a un gimnasio.");
        }
        if (this.registeredByAdminId == null) {
            throw new InvalidSaleException("Se debe registrar quién realizó la venta.");
        }
        if (this.details.isEmpty()) {
            throw new InvalidSaleException("La venta debe tener al menos un producto (detalle).");
        }
    }

    public static Sale createNew(PaymentMethod paymentMethod, Long gymId, Long registeredByAdminId,
                                 Long memberId, List<SaleDetail> details) {
        // La venta calcula su propio total sumando los subtotales de cada detalle
        BigDecimal total = details != null ? details.stream()
                .map(SaleDetail::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add) : BigDecimal.ZERO;

        return new Sale(null, LocalDateTime.now(), total, paymentMethod, gymId, registeredByAdminId, memberId, details);
    }

    public static Sale restore(Long id, LocalDateTime saleDate, BigDecimal totalAmount, PaymentMethod paymentMethod,
                               Long gymId, Long registeredByAdminId, Long memberId, List<SaleDetail> details) {
        return new Sale(id, saleDate, totalAmount, paymentMethod, gymId, registeredByAdminId, memberId, details);
    }
}