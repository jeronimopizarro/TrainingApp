package com.trainingapp.trainingapp.domain.entity.routine;

import com.trainingapp.trainingapp.domain.enums.routine.RoutineStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RoutineTest {

    private Routine createRestoredRoutineWithTwoDays() {
        TrainingDay day1 = TrainingDay.restore(10L, "Día 1", 1, List.of());
        TrainingDay day2 = TrainingDay.restore(11L, "Día 2", 2, List.of());

        return Routine.restore(
                1L, "Rutina Test", null, null, 1L, 2L, 2L, 1L,
                RoutineStatus.ACTIVE, true, List.of(day1, day2)
        );
    }

    @Test
    @DisplayName("Debería retornar el primer día si el usuario nunca entrenó")
    void getNextTrainingDay_WhenNeverTrained_ReturnsFirstDay() {
        Routine routine = createRestoredRoutineWithTwoDays();

        TrainingDay nextDay = routine.getNextTrainingDay(null);

        assertEquals(10L, nextDay.getId());
    }

    @Test
    @DisplayName("Debería retornar el siguiente día cuando se completó el anterior")
    void getNextTrainingDay_WhenLastDayWasCompleted_ReturnsNextDay() {
        Routine routine = createRestoredRoutineWithTwoDays();

        TrainingDay nextDay = routine.getNextTrainingDay(10L);

        assertEquals(11L, nextDay.getId());
    }

    @Test
    @DisplayName("Debería reiniciar el ciclo y retornar el primer día al terminar la semana")
    void getNextTrainingDay_WhenEndOfCycle_ReturnsFirstDay() {
        Routine routine = createRestoredRoutineWithTwoDays();

        TrainingDay nextDay = routine.getNextTrainingDay(11L);

        assertEquals(10L, nextDay.getId());
    }

    @Test
    @DisplayName("Debería retornar el primer día si el ID enviado no pertenece a la rutina")
    void getNextTrainingDay_WhenUnknownDayId_ReturnsFirstDay() {
        Routine routine = createRestoredRoutineWithTwoDays();

        // Enviamos un ID inventado (99L) simulando un dato corrupto o viejo
        TrainingDay nextDay = routine.getNextTrainingDay(99L);

        assertEquals(10L, nextDay.getId());
    }

    @Test
    @DisplayName("Debería retornar null si la rutina no tiene días configurados")
    void getNextTrainingDay_WhenNoDays_ReturnsNull() {
        Routine routine = Routine.createNew("Rutina Vacía", 1L, 2L, 2L, 1L);

        TrainingDay nextDay = routine.getNextTrainingDay(null);

        assertNull(nextDay);
    }
}
