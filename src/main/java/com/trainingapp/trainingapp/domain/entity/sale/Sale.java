package com.trainingapp.trainingapp.domain.entity.sale;

import com.trainingapp.trainingapp.domain.enums.transaction.PaymentMethod;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class Sale {

    private Long id;
    private LocalDateTime saleDate;
    private BigDecimal totalAmount;
    private PaymentMethod paymentMethod;
    private Long gymId;
    private Long registeredByAdminId;
    private Long memberId;
    private List<SaleDetail> details;

    public Sale(Long id, LocalDateTime saleDate, BigDecimal totalAmount, PaymentMethod paymentMethod,
                Long gymId, Long registeredByAdminId, Long memberId, List<SaleDetail> details) {
        validateSale(paymentMethod, gymId, registeredByAdminId, details);

        this.id = id;
        this.saleDate = saleDate;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.gymId = gymId;
        this.registeredByAdminId = registeredByAdminId;
        this.memberId = memberId;
        this.details = details;
    }

    public static Sale createNew(PaymentMethod paymentMethod, Long gymId, Long registeredByAdminId,
                                 Long memberId, List<SaleDetail> details) {

        validateSale(paymentMethod, gymId, registeredByAdminId, details);

        // La venta calcula su propio total sumando los subtotales de cada detalle
        BigDecimal total = details.stream()
                .map(SaleDetail::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new Sale(null, LocalDateTime.now(), total, paymentMethod, gymId, registeredByAdminId, memberId, details);
    }

    private static void validateSale(PaymentMethod paymentMethod, Long gymId, Long registeredByAdminId,
                                     List<SaleDetail> details) {
        if (paymentMethod == null) {
            throw new IllegalArgumentException("El método de pago es obligatorio.");
        }
        if (gymId == null) {
            throw new IllegalArgumentException("La venta debe estar asociada a un gimnasio.");
        }
        if (registeredByAdminId == null) {
            throw new IllegalArgumentException("Se debe registrar quién realizó la venta.");
        }
        if (details == null || details.isEmpty()) {
            throw new IllegalArgumentException("La venta debe tener al menos un producto (detalle).");
        }
    }
}