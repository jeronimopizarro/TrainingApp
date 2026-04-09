package com.trainingapp.trainingapp.domain.entity.membership;

import com.trainingapp.trainingapp.domain.exception.membership.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MembershipPlanTest {

    @Test
    @DisplayName("Debería crear un plan de membresía válido y activo")
    void shouldCreateValidMembershipPlan() {
        MembershipPlan plan = MembershipPlan.createNew(
                "Plan Anual", "Acceso total", new BigDecimal("12000.0"), 12, 10L
        );

        assertNotNull(plan);
        assertEquals("Plan Anual", plan.getName());
        assertEquals("Acceso total", plan.getDescription());
        assertEquals(new BigDecimal("12000.0"), plan.getPrice());
        assertEquals(12, plan.getDurationMonths());
        assertEquals(10L, plan.getGymId());
        assertTrue(plan.isActive());
    }

    @Test
    @DisplayName("Debería lanzar error si el nombre del plan es nulo o vacío")
    void shouldThrowExceptionWhenNameIsInvalid() {
        assertThrows(MembershipPlanNameRequiredException.class, () ->
                MembershipPlan.createNew(null, "Desc", new BigDecimal("100"), 1, 10L)
        );

        assertThrows(MembershipPlanNameRequiredException.class, () ->
                MembershipPlan.createNew("   ", "Desc", new BigDecimal("100"), 1, 10L)
        );
    }

    @Test
    @DisplayName("Debería lanzar error si el precio es nulo o negativo")
    void shouldThrowExceptionWhenPriceIsInvalid() {
        assertThrows(NegativeMembershipPriceException.class, () ->
                MembershipPlan.createNew("Plan", "Desc", null, 1, 10L)
        );

        assertThrows(NegativeMembershipPriceException.class, () ->
                MembershipPlan.createNew("Plan", "Desc", new BigDecimal("-10.0"), 1, 10L)
        );
    }

    @Test
    @DisplayName("Debería lanzar error si la duración es nula o menor a 1")
    void shouldThrowExceptionWhenDurationIsInvalid() {
        assertThrows(InvalidMembershipDurationException.class, () ->
                MembershipPlan.createNew("Plan", "Desc", new BigDecimal("100"), null, 10L)
        );

        assertThrows(InvalidMembershipDurationException.class, () ->
                MembershipPlan.createNew("Plan", "Desc", new BigDecimal("100"), 0, 10L)
        );
    }

    @Test
    @DisplayName("Debería actualizar los detalles exitosamente")
    void shouldUpdateDetails() {
        MembershipPlan plan = MembershipPlan.createNew("Plan", "Desc", new BigDecimal("100"), 1, 10L);

        plan.updateDetails("Plan Pro", "Desc Pro", new BigDecimal("200.0"), 3);

        assertEquals("Plan Pro", plan.getName());
        assertEquals(new BigDecimal("200.0"), plan.getPrice());
        assertEquals(3, plan.getDurationMonths());
    }

    @Test
    @DisplayName("Debería desactivar y reactivar el plan validando sus estados")
    void shouldDeactivateAndReactivatePlan() {
        MembershipPlan plan = MembershipPlan.createNew("Plan", "Desc", new BigDecimal("100"), 1, 10L);

        plan.deactivate();
        assertFalse(plan.isActive());

        assertThrows(MembershipAlreadyInactiveException.class, plan::deactivate);

        plan.activate();
        assertTrue(plan.isActive());

        assertThrows(MembershipAlreadyActiveException.class, plan::activate);
    }
}