package com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.sale;

import com.trainingapp.trainingapp.domain.enums.transaction.PaymentMethod;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.BaseJpaEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaleJpaEntity extends BaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sale_id")
    private Long id;

    @Column(name = "sale_date", nullable = false)
    private LocalDateTime saleDate;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 50)
    private PaymentMethod paymentMethod;

    @Column(name = "gym_id", nullable = false)
    private Long gymId;

    @Column(name = "registered_by_admin_id", nullable = false)
    private Long registeredByAdminId;

    @Column(name = "member_id")
    private Long memberId;

    // CascadeType.ALL y orphanRemoval aseguran que si se borra la Venta, se borran sus Detalles automáticamente.
    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleDetailJpaEntity> details = new ArrayList<>();

    public void addDetail(SaleDetailJpaEntity detail) {
        details.add(detail);
        detail.setSale(this);
    }
}
