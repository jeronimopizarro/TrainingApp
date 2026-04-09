package com.trainingapp.trainingapp.application.useCase.exercise;

import com.trainingapp.trainingapp.application.validator.ExerciseAccessValidator;
import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.domain.exception.exercise.ExerciseNotFoundException;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteExerciseUseCaseTest {

    @Mock private ExerciseRepository exerciseRepository;
    @Mock private ExerciseAccessValidator exerciseAccessValidator;

    @InjectMocks private DeleteExerciseUseCase useCase;

    @Test
    @DisplayName("Debería desactivar un ejercicio exitosamente")
    void shouldDeactivateExerciseSuccessfully() {
        Long exerciseId = 1L;
        Exercise mockExercise = mock(Exercise.class);

        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(mockExercise));
        doNothing().when(exerciseAccessValidator).validateWriteAccess(mockExercise);

        useCase.execute(exerciseId);

        verify(mockExercise).deactivate();
        verify(exerciseRepository).save(mockExercise);
    }

    @Test
    @DisplayName("Debería lanzar error si el ejercicio no existe al intentar borrar")
    void shouldThrowExceptionWhenNotFound() {
        Long exerciseId = 99L;
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.empty());

        assertThrows(ExerciseNotFoundException.class, () -> useCase.execute(exerciseId));
        verifyNoInteractions(exerciseAccessValidator);
    }
}