package com.trainingapp.trainingapp.domain.entity.exercise;

import com.trainingapp.trainingapp.domain.exception.exercise.CustomExerciseRequiresGymException;
import com.trainingapp.trainingapp.domain.exception.exercise.ExerciseAlreadyActiveException;
import com.trainingapp.trainingapp.domain.exception.exercise.ExerciseAlreadyInactiveException;
import com.trainingapp.trainingapp.domain.exception.exercise.ExerciseNameRequiredException;
import com.trainingapp.trainingapp.domain.exception.muscleGroup.DuplicateMuscleGroupException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExerciseTest {

    @Test
    @DisplayName("Debería crear un ejercicio base válido")
    void shouldCreateValidBaseExercise() {
        Exercise exercise = Exercise.createNew(
                "Sentadilla Libre", "Desc", "imgUrl", "vidUrl", true, 1L, null
        );

        assertNotNull(exercise);
        assertEquals("Sentadilla Libre", exercise.getName());
        assertEquals("Desc", exercise.getDescription());
        assertEquals("imgUrl", exercise.getImageUrl());
        assertEquals("vidUrl", exercise.getVideoUrl());
        assertTrue(exercise.getIsBase());
        assertNull(exercise.getGymId());
        assertEquals(1L, exercise.getCreatedByUserId());
        assertTrue(exercise.isActive());
        assertTrue(exercise.getMuscleGroups().isEmpty());
    }

    @Test
    @DisplayName("Debería crear un ejercicio custom válido para un gimnasio")
    void shouldCreateValidCustomExercise() {
        Long gymId = 10L;
        Exercise exercise = Exercise.createNew(
                "Remo Custom", "Desc", "img", "vid", false, 1L, gymId
        );

        assertNotNull(exercise);
        assertEquals("Remo Custom", exercise.getName());
        assertFalse(exercise.getIsBase());
        assertEquals(gymId, exercise.getGymId());
        assertTrue(exercise.isActive());
    }

    @Test
    @DisplayName("Debería lanzar error si el nombre del ejercicio es nulo o vacío al crear")
    void shouldThrowExceptionWhenNameIsInvalid() {
        assertThrows(ExerciseNameRequiredException.class, () ->
                Exercise.createNew(null, "Desc", "img", "vid", true, 1L, null)
        );

        assertThrows(ExerciseNameRequiredException.class, () ->
                Exercise.createNew("   ", "Desc", "img", "vid", false, 1L, 10L)
        );
    }

    @Test
    @DisplayName("Debería lanzar error si se crea un ejercicio custom sin asignarle un gimnasio")
    void shouldThrowExceptionWhenCustomExerciseLacksGym() {
        assertThrows(CustomExerciseRequiresGymException.class, () ->
                Exercise.createNew("Remo Custom", "Desc", "img", "vid", false, 1L, null)
        );
    }

    @Test
    @DisplayName("Debería actualizar los detalles básicos del ejercicio exitosamente")
    void shouldUpdateDetails() {
        Exercise exercise = Exercise.createNew("Sentadilla", "Desc", "img", "vid", true, 1L, null);

        exercise.updateDetails("Sentadilla Profunda", "Nueva desc", "newImg", "newVid");

        assertEquals("Sentadilla Profunda", exercise.getName());
        assertEquals("Nueva desc", exercise.getDescription());
        assertEquals("newImg", exercise.getImageUrl());
        assertEquals("newVid", exercise.getVideoUrl());
    }

    @Test
    @DisplayName("Debería lanzar error al intentar actualizar con un nombre inválido")
    void shouldThrowExceptionWhenUpdatingWithInvalidName() {
        Exercise exercise = Exercise.createNew("Press Banca", "Desc", "img", "vid", true, 1L, null);

        assertThrows(ExerciseNameRequiredException.class, () ->
                exercise.updateDetails("   ", "Nueva desc", "img", "vid")
        );
    }

    @Test
    @DisplayName("Debería agregar grupos musculares y manejar correctamente el grupo primario")
    void shouldAddMuscleGroupsAndHandlePrimaryLogic() {
        Exercise exercise = Exercise.createNew("Dominadas", "Desc", "img", "vid", true, 1L, null);

        Long espaldaId = 1L;
        Long bicepsId = 2L;

        // Primero agregamos Bíceps como primario
        exercise.addMuscleGroup(bicepsId, true);
        assertEquals(1, exercise.getMuscleGroups().size());
        assertTrue(exercise.getMuscleGroups().get(0).isPrimary());

        // Ahora agregamos Espalda como primario (debería volver secundario al Bíceps)
        exercise.addMuscleGroup(espaldaId, true);

        assertEquals(2, exercise.getMuscleGroups().size());

        boolean isEspaldaPrimary = exercise.getMuscleGroups().stream()
                .anyMatch(mg -> mg.getMuscleGroupId().equals(espaldaId) && mg.isPrimary());
        boolean isBicepsPrimary = exercise.getMuscleGroups().stream()
                .anyMatch(mg -> mg.getMuscleGroupId().equals(bicepsId) && mg.isPrimary());

        assertTrue(isEspaldaPrimary, "Espalda debería ser el primario ahora");
        assertFalse(isBicepsPrimary, "Bíceps debería haber pasado a secundario");
    }

    @Test
    @DisplayName("Debería lanzar error al intentar agregar un grupo muscular duplicado")
    void shouldThrowExceptionWhenAddingDuplicateMuscleGroup() {
        Exercise exercise = Exercise.createNew("Dominadas", "Desc", "img", "vid", true, 1L, null);
        exercise.addMuscleGroup(1L, true);

        assertThrows(DuplicateMuscleGroupException.class, () ->
                exercise.addMuscleGroup(1L, false)
        );
    }

    @Test
    @DisplayName("Debería limpiar todos los grupos musculares")
    void shouldClearAllMuscleGroups() {
        Exercise exercise = Exercise.createNew("Dominadas", "Desc", "img", "vid", true, 1L, null);
        exercise.addMuscleGroup(1L, true);

        exercise.clearMuscleGroups();

        assertTrue(exercise.getMuscleGroups().isEmpty());
    }

    @Test
    @DisplayName("Debería actualizar el estado base y anular el gymId si pasa a ser Base")
    void shouldUpdateBaseStatus() {
        Exercise exercise = Exercise.createNew("Remo", "Desc", "img", "vid", false, 1L, 10L);

        exercise.updateBaseStatus(true);

        assertTrue(exercise.getIsBase());
        assertNull(exercise.getGymId());
    }

    @Test
    @DisplayName("Debería desactivar y reactivar el ejercicio validando sus estados")
    void shouldDeactivateAndReactivateExercise() {
        Exercise exercise = Exercise.createNew("Dominadas", "Desc", "img", "vid", true, 1L, null);

        exercise.deactivate();
        assertFalse(exercise.isActive());
        assertThrows(ExerciseAlreadyInactiveException.class, exercise::deactivate);

        exercise.activate();
        assertTrue(exercise.isActive());
        assertThrows(ExerciseAlreadyActiveException.class, exercise::activate);
    }
}