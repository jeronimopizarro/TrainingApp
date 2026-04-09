package com.trainingapp.trainingapp.domain.entity.routine;

import com.trainingapp.trainingapp.domain.exception.routine.InvalidRoutineMetricsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoutineDetailTest {

    @Nested
    @DisplayName("Validaciones de creación y actualización (Métricas)")
    class ValidationTests {

        @Test
        @DisplayName("Debería crear un detalle de rutina válido exitosamente")
        void shouldCreateValidRoutineDetail() {
            RoutineDetail detail = RoutineDetail.createNew(
                    1L, 1, 4, 8, 12, 2, 50.0, "Nota"
            );

            assertNotNull(detail);
            assertEquals(1L, detail.getExerciseId());
            assertEquals(4, detail.getSets());
        }

        @Test
        @DisplayName("Debería lanzar error si el ID del ejercicio es nulo o inválido")
        void shouldThrowException_WhenExerciseIdIsInvalid() {
            assertThrows(InvalidRoutineMetricsException.class, () ->
                    RoutineDetail.createNew(null, 1, 4, 8, 12, 2, 50.0, "Nota")
            );
            assertThrows(InvalidRoutineMetricsException.class, () ->
                    RoutineDetail.createNew(0L, 1, 4, 8, 12, 2, 50.0, "Nota")
            );
        }

        @Test
        @DisplayName("Debería lanzar error si las series o repeticiones son menores a 1")
        void shouldThrowException_WhenSetsOrRepsAreZero() {
            // Series = 0
            assertThrows(InvalidRoutineMetricsException.class, () ->
                    RoutineDetail.createNew(1L, 1, 0, 8, 12, 2, 50.0, "")
            );
            // Reps mínimas = 0
            assertThrows(InvalidRoutineMetricsException.class, () ->
                    RoutineDetail.createNew(1L, 1, 4, 0, 12, 2, 50.0, "")
            );
        }

        @Test
        @DisplayName("Debería lanzar error si las reps máximas son menores a las mínimas")
        void shouldThrowException_WhenMaxRepsLowerThanMinReps() {
            assertThrows(InvalidRoutineMetricsException.class, () ->
                    // 12 min, 8 max (Invalido)
                    RoutineDetail.createNew(1L, 1, 4, 12, 8, 2, 50.0, "")
            );
        }

        @Test
        @DisplayName("Debería lanzar error si RIR o Peso Sugerido son negativos")
        void shouldThrowException_WhenRirOrWeightIsNegative() {
            // RIR negativo
            assertThrows(InvalidRoutineMetricsException.class, () ->
                    RoutineDetail.createNew(1L, 1, 4, 8, 12, -1, 50.0, "")
            );
            // Peso negativo
            assertThrows(InvalidRoutineMetricsException.class, () ->
                    RoutineDetail.createNew(1L, 1, 4, 8, 12, 2, -5.0, "")
            );
        }

        @Test
        @DisplayName("Debería validar nuevamente al actualizar (update)")
        void shouldValidate_WhenUpdating() {
            RoutineDetail detail = RoutineDetail.createNew(1L, 1, 4, 8, 12, 2, 50.0, "Nota");

            // Intentamos actualizar con un peso negativo
            assertThrows(InvalidRoutineMetricsException.class, () ->
                    detail.update(1L, 4, 8, 12, 2, -10.0, "Nota modificada")
            );
        }
    }
}