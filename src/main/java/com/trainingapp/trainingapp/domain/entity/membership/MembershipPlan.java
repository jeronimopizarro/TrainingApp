package com.trainingapp.trainingapp.domain.entity.membership;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class MembershipPlan {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer durationMonths;
    private Long gymId;
    private boolean active;

    public MembershipPlan(String name, String description, BigDecimal price, Integer durationMonths, Long gymId) {
        validateFields(name, price, durationMonths);
        if (gymId == null) {
            throw new IllegalArgumentException("El plan debe pertenecer a un gimnasio (gymId no puede ser null).");
        }

        this.name = name;
        this.description = description;
        this.price = price;
        this.durationMonths = durationMonths;
        this.gymId = gymId;
        this.active = true;
    }

    private void validateFields(String name, BigDecimal price, Integer durationMonths) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre del plan es obligatorio.");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }
        if (durationMonths == null || durationMonths <= 0) {
            throw new IllegalArgumentException("La duración del plan debe ser mayor a 0 días.");
        }
    }

    public void update(String newName, String newDescription, BigDecimal newPrice, Integer newDurationMonths) {
        validateFields(newName, newPrice, newDurationMonths);

        this.name = newName;
        this.description = newDescription;
        this.price = newPrice;
        this.durationMonths = newDurationMonths;
    }

    public void deactivate() {
        if (!this.active) {
            throw new IllegalStateException("El plan ya se encuentra desactivado.");
        }
        this.active = false;
    }

    public void setId(Long id) { this.id = id; }
}
