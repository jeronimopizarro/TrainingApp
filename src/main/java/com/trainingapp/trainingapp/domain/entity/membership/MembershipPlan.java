package com.trainingapp.trainingapp.domain.entity.membership;

import com.trainingapp.trainingapp.domain.exception.membership.*;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class MembershipPlan {

    private final Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer durationMonths;
    private final Long gymId;
    private boolean active;

    private MembershipPlan(Long id, String name, String description, BigDecimal price, Integer durationMonths, Long gymId, boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.durationMonths = durationMonths;
        this.gymId = gymId;
        this.active = active;
        validate();
    }

    private void validate() {
        if (this.name == null || this.name.trim().isEmpty()) {
            throw new MembershipPlanNameRequiredException();
        }
        if (this.price == null || this.price.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeMembershipPriceException();
        }
        if (this.durationMonths == null || this.durationMonths < 1) {
            throw new InvalidMembershipDurationException();
        }
    }

    public static MembershipPlan createNew(String name, String description, BigDecimal price, Integer durationMonths, Long gymId) {
        return new MembershipPlan(null, name, description, price, durationMonths, gymId, true);
    }

    public static MembershipPlan restore(Long id, String name, String description, BigDecimal price, Integer durationMonths, Long gymId, boolean active) {
        return new MembershipPlan(id, name, description, price, durationMonths, gymId, active);
    }

    public void updateDetails(String name, String description, BigDecimal price, Integer durationMonths) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.durationMonths = durationMonths;
        validate();
    }

    public void deactivate() {
        if (!this.active) {
            throw new MembershipAlreadyInactiveException();
        }
        this.active = false;
    }

    public void activate() {
        if (this.active) {
            throw new MembershipAlreadyActiveException();
        }
        this.active = true;
    }
}
