package com.trainingapp.trainingapp.application.useCase.exercise;

import com.trainingapp.trainingapp.application.mapper.exercise.ExerciseDTOMapper;
import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.exception.exercise.ExerciseNotFoundException;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.exercise.ExerciseDetailResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetExerciseByIdUseCaseTest {

    @Mock private ExerciseRepository exerciseRepository;
    @Mock private SecurityUtils securityUtils;
    @Mock private ExerciseDTOMapper exerciseDTOMapper;

    @InjectMocks private GetExerciseByIdUseCase useCase;

    @Test
    @DisplayName("Debería retornar un ejercicio base exitosamente para cualquier usuario")
    void shouldReturnBaseExerciseById() {
        Long exerciseId = 1L;
        User mockUser = mock(User.class);
        Exercise mockExercise = mock(Exercise.class);
        ExerciseDetailResponse mockResponse = mock(ExerciseDetailResponse.class);

        when(securityUtils.getCurrentUser()).thenReturn(mockUser);
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(mockExercise));

        when(mockUser.isSuperAdmin()).thenReturn(false);
        when(mockExercise.getIsBase()).thenReturn(true); // Es base, pasa directo sin validar gym

        when(exerciseDTOMapper.toDetailResponse(mockExercise)).thenReturn(mockResponse);

        ExerciseDetailResponse response = useCase.execute(exerciseId);

        assertNotNull(response);
        verify(securityUtils, never()).validateSameGym(any());
    }

    @Test
    @DisplayName("Debería retornar un ejercicio custom exitosamente tras validar el gimnasio")
    void shouldReturnCustomExerciseById() {
        Long exerciseId = 1L;
        Long gymId = 10L;
        User mockUser = mock(User.class);
        Exercise mockExercise = mock(Exercise.class);
        ExerciseDetailResponse mockResponse = mock(ExerciseDetailResponse.class);

        when(securityUtils.getCurrentUser()).thenReturn(mockUser);
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(mockExercise));

        when(mockUser.isSuperAdmin()).thenReturn(false);
        when(mockExercise.getIsBase()).thenReturn(false); // Es custom, requiere validar gym

        when(mockExercise.getGymId()).thenReturn(gymId);
        doNothing().when(securityUtils).validateSameGym(gymId);

        when(exerciseDTOMapper.toDetailResponse(mockExercise)).thenReturn(mockResponse);

        ExerciseDetailResponse response = useCase.execute(exerciseId);

        assertNotNull(response);
        verify(securityUtils).validateSameGym(gymId);
    }

    @Test
    @DisplayName("Debería lanzar error si el ejercicio no existe")
    void shouldThrowExceptionWhenExerciseNotFound() {
        Long exerciseId = 99L;
        User mockUser = mock(User.class);

        when(securityUtils.getCurrentUser()).thenReturn(mockUser);
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.empty());

        assertThrows(ExerciseNotFoundException.class, () -> useCase.execute(exerciseId));
    }
}