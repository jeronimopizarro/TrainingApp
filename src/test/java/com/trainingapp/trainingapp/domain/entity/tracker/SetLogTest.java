package com.trainingapp.trainingapp.domain.entity.tracker;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SetLogTest {

    @Test
    @DisplayName("Debería calcular correctamente le e1RM con RIR")
    void calculateEstimated1RMNormally() {
        // Simulamos que el alumno levantó 100 kg, hizo 5 repeticiones y dejó 1 en el tanque (RIR 1).
        SetLog stLog = SetLog.createNew(1L, 1, 5, BigDecimal.valueOf(100), 1, "Buena tecnica");

        BigDecimal e1rm =  stLog.calculateEstimated1RM();

        assertEquals(BigDecimal.valueOf(120.0), e1rm.setScale(1));
    }

    @Test
    @DisplayName("Si hace 1 sola repetición al límite (RIR 0), el e1RM es el peso exacto")
    void calculateEstimated1RMForSingleMaxRep() {
        SetLog setLog = SetLog.createNew(1L, 1, 1, BigDecimal.valueOf(85.5), 0, "Al límite");

        BigDecimal e1rm = setLog.calculateEstimated1RM();

        assertEquals(BigDecimal.valueOf(85.5), e1rm);
    }

    @Test
    @DisplayName("Debería retornar 0 si el peso levantado es nulo o cero")
    void returnZeroWhenWeightIsZero() {
        SetLog setLog = SetLog.createNew(1L, 1, 10, BigDecimal.ZERO, 2, "Calentamiento sin peso");

        BigDecimal e1rm = setLog.calculateEstimated1RM();

        assertEquals(BigDecimal.ZERO, e1rm);
    }
}
