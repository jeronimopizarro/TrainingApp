package com.trainingapp.trainingapp.application.validator;

import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.exception.exercise.UnauthorizedBaseExerciseModificationException;
import com.trainingapp.trainingapp.domain.exception.exercise.UnauthorizedExerciseModificationException;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExerciseAccessValidatorTest {

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private ExerciseAccessValidator validator;

    @Test
    @DisplayName("SuperAdmin puede modificar cualquier ejercicio")
    void superAdminCanModifyAnyExercise() {
        User currentUser = mock(User.class);
        when(securityUtils.getCurrentUser()).thenReturn(currentUser);
        when(currentUser.isSuperAdmin()).thenReturn(true);

        Exercise exercise = mock(Exercise.class);

        assertDoesNotThrow(() -> validator.validateWriteAccess(exercise));
    }

    @Test
    @DisplayName("No SuperAdmin no puede modificar ejercicio Base")
    void normalUserCannotModifyBaseExercise() {
        User currentUser = mock(User.class);
        when(securityUtils.getCurrentUser()).thenReturn(currentUser);
        when(currentUser.isSuperAdmin()).thenReturn(false);

        Exercise exercise = mock(Exercise.class);
        when(exercise.getIsBase()).thenReturn(true);

        assertThrows(UnauthorizedBaseExerciseModificationException.class, () -> validator.validateWriteAccess(exercise));
    }

    @Test
    @DisplayName("Entrenador solo puede modificar sus propios ejercicios custom")
    void trainerCanOnlyModifyOwnExercises() {
        User currentUser = mock(User.class);
        when(securityUtils.getCurrentUser()).thenReturn(currentUser);
        when(currentUser.isSuperAdmin()).thenReturn(false);
        when(currentUser.isTrainer()).thenReturn(true);
        when(currentUser.getId()).thenReturn(1L);

        Exercise exercise = mock(Exercise.class);
        when(exercise.getIsBase()).thenReturn(false);
        when(exercise.getGymId()).thenReturn(10L);
        when(exercise.getCreatedByUserId()).thenReturn(2L); // Creado por otro usuario

        doNothing().when(securityUtils).validateSameGym(10L);

        assertThrows(UnauthorizedExerciseModificationException.class, () -> validator.validateWriteAccess(exercise));

        when(exercise.getCreatedByUserId()).thenReturn(1L); // Creado por él mismo
        assertDoesNotThrow(() -> validator.validateWriteAccess(exercise));
    }
}