package com.trainingapp.trainingapp.domain.entity.exercise;

import com.trainingapp.trainingapp.domain.exception.muscleGroup.MuscleGroupNameRequiredException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MuscleGroupTest {

    @Test
    @DisplayName("Debería crear un grupo muscular válido")
    void shouldCreateValidMuscleGroup() {
        MuscleGroup muscleGroup = MuscleGroup.createNew("Pecho", "Músculos pectorales");

        assertNotNull(muscleGroup);
        assertNull(muscleGroup.getId());
        assertEquals("Pecho", muscleGroup.getName());
        assertEquals("Músculos pectorales", muscleGroup.getDescription());
    }

    @Test
    @DisplayName("Debería restaurar un grupo muscular con ID")
    void shouldRestoreMuscleGroup() {
        MuscleGroup muscleGroup = MuscleGroup.restore(1L, "Espalda", "Músculos dorsales");

        assertNotNull(muscleGroup);
        assertEquals(1L, muscleGroup.getId());
        assertEquals("Espalda", muscleGroup.getName());
        assertEquals("Músculos dorsales", muscleGroup.getDescription());
    }

    @Test
    @DisplayName("Debería lanzar error si el nombre es nulo o vacío al crear")
    void shouldThrowExceptionWhenNameIsInvalid() {
        assertThrows(MuscleGroupNameRequiredException.class, () ->
                MuscleGroup.createNew(null, "Descripción")
        );

        assertThrows(MuscleGroupNameRequiredException.class, () ->
                MuscleGroup.createNew("", "Descripción")
        );

        assertThrows(MuscleGroupNameRequiredException.class, () ->
                MuscleGroup.createNew("   ", "Descripción")
        );
    }

    @Test
    @DisplayName("Debería actualizar los detalles exitosamente")
    void shouldUpdateDetails() {
        MuscleGroup muscleGroup = MuscleGroup.createNew("Pecho", "Descripción antigua");

        muscleGroup.updateDetails("Pecho Superior", "Nueva descripción");

        assertEquals("Pecho Superior", muscleGroup.getName());
        assertEquals("Nueva descripción", muscleGroup.getDescription());
    }

    @Test
    @DisplayName("Debería lanzar error al intentar actualizar con un nombre nulo o vacío")
    void shouldThrowExceptionWhenUpdatingWithInvalidName() {
        MuscleGroup muscleGroup = MuscleGroup.createNew("Piernas", "Descripción");

        assertThrows(MuscleGroupNameRequiredException.class, () ->
                muscleGroup.updateDetails(null, "Nueva desc")
        );
    }
}