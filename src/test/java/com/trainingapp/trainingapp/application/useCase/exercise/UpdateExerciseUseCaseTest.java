package com.trainingapp.trainingapp.application.useCase.exercise;

import com.trainingapp.trainingapp.application.mapper.exercise.ExerciseDTOMapper;
import com.trainingapp.trainingapp.application.validator.ExerciseAccessValidator;
import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.domain.entity.exercise.MuscleGroup;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.exception.exercise.BaseExerciseAlreadyExistsException;
import com.trainingapp.trainingapp.domain.exception.exercise.GymExerciseAlreadyExistsException;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import com.trainingapp.trainingapp.domain.repository.exercise.MuscleGroupRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.exercise.ExerciseResponse;
import com.trainingapp.trainingapp.web.dto.exercise.UpdateExerciseRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateExerciseUseCaseTest {

    @Mock private ExerciseRepository exerciseRepository;
    @Mock private MuscleGroupRepository muscleGroupRepository;
    @Mock private SecurityUtils securityUtils;
    @Mock private ExerciseDTOMapper exerciseDTOMapper;
    @Mock private ExerciseAccessValidator exerciseAccessValidator;

    @InjectMocks private UpdateExerciseUseCase useCase;

    @Test
    @DisplayName("Debería actualizar un ejercicio exitosamente")
    void shouldUpdateExerciseSuccessfully() {
        Long exerciseId = 1L;
        UpdateExerciseRequest request = new UpdateExerciseRequest(
                "Nuevo Nombre", "Nueva Desc", "url_img", "url_vid", false,
                List.of(new UpdateExerciseRequest.MuscleGroupAssignmentRequest(1L, true))
        );
        User mockUser = mock(User.class);
        Exercise mockExercise = mock(Exercise.class);
        MuscleGroup mockMuscleGroup = mock(MuscleGroup.class);

        when(securityUtils.getCurrentUser()).thenReturn(mockUser);
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(mockExercise));

        doNothing().when(exerciseAccessValidator).validateWriteAccess(mockExercise);

        when(mockExercise.getIsBase()).thenReturn(true);
        when(exerciseRepository.existsBaseExerciseByNameAndIdNot("Nuevo Nombre", exerciseId)).thenReturn(false);

        when(muscleGroupRepository.findById(1L)).thenReturn(Optional.of(mockMuscleGroup));
        when(mockUser.isSuperAdmin()).thenReturn(false); // No intentará actualizar estado Base

        when(exerciseRepository.save(mockExercise)).thenReturn(mockExercise);
        when(exerciseDTOMapper.toResponse(mockExercise)).thenReturn(mock(ExerciseResponse.class));

        ExerciseResponse response = useCase.execute(exerciseId, request);

        assertNotNull(response);
        verify(mockExercise).updateDetails("Nuevo Nombre", "Nueva Desc", "url_img", "url_vid");
        verify(mockExercise).clearMuscleGroups();
        verify(mockExercise).addMuscleGroup(1L, true);
        verify(exerciseRepository).save(mockExercise);
    }

    @Test
    @DisplayName("Debería lanzar error si el nuevo nombre ya pertenece a otro ejercicio base")
    void shouldThrowExceptionWhenBaseNameDuplicated() {
        Long exerciseId = 1L;
        UpdateExerciseRequest request = new UpdateExerciseRequest("Nombre Existente", "Desc", "img", "vid", true, List.of());
        User mockUser = mock(User.class);
        Exercise mockExercise = mock(Exercise.class);

        when(securityUtils.getCurrentUser()).thenReturn(mockUser);
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(mockExercise));
        doNothing().when(exerciseAccessValidator).validateWriteAccess(mockExercise);

        when(mockExercise.getIsBase()).thenReturn(true);
        when(exerciseRepository.existsBaseExerciseByNameAndIdNot("Nombre Existente", exerciseId)).thenReturn(true);

        assertThrows(BaseExerciseAlreadyExistsException.class, () -> useCase.execute(exerciseId, request));
        verify(exerciseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar error si el nuevo nombre ya pertenece a otro ejercicio custom en el gym")
    void shouldThrowExceptionWhenCustomNameDuplicated() {
        Long exerciseId = 1L;
        Long gymId = 10L;
        UpdateExerciseRequest request = new UpdateExerciseRequest("Nombre Existente", "Desc", "img", "vid", false, List.of());
        User mockUser = mock(User.class);
        Exercise mockExercise = mock(Exercise.class);

        when(securityUtils.getCurrentUser()).thenReturn(mockUser);
        when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(mockExercise));
        doNothing().when(exerciseAccessValidator).validateWriteAccess(mockExercise);

        when(mockExercise.getIsBase()).thenReturn(false);
        when(mockExercise.getGymId()).thenReturn(gymId);
        when(exerciseRepository.existsByNameAndGymIdAndIdNot("Nombre Existente", gymId, exerciseId)).thenReturn(true);

        assertThrows(GymExerciseAlreadyExistsException.class, () -> useCase.execute(exerciseId, request));
        verify(exerciseRepository, never()).save(any());
    }
}