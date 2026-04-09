package com.trainingapp.trainingapp.application.useCase.exercise;

import com.trainingapp.trainingapp.application.mapper.exercise.ExerciseDTOMapper;
import com.trainingapp.trainingapp.application.validator.GymValidator;
import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.domain.entity.exercise.MuscleGroup;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.exception.exercise.BaseExerciseAlreadyExistsException;
import com.trainingapp.trainingapp.domain.exception.exercise.GymExerciseAlreadyExistsException;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import com.trainingapp.trainingapp.domain.repository.exercise.MuscleGroupRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.exercise.CreateExerciseRequest;
import com.trainingapp.trainingapp.web.dto.exercise.ExerciseResponse;
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
class CreateExerciseUseCaseTest {

    @Mock private ExerciseRepository exerciseRepository;
    @Mock private MuscleGroupRepository muscleGroupRepository;
    @Mock private SecurityUtils securityUtils;
    @Mock private GymValidator gymValidator;
    @Mock private ExerciseDTOMapper exerciseDTOMapper;

    @InjectMocks private CreateExerciseUseCase useCase;

    @Test
    @DisplayName("Debería crear un ejercicio base (global) exitosamente si es SuperAdmin")
    void shouldCreateBaseExerciseSuccessfully() {
        CreateExerciseRequest request = new CreateExerciseRequest(
                "Sentadilla", "Desc", "img", "vid", true,
                List.of(new CreateExerciseRequest.MuscleGroupAssignmentRequest(1L, true))
        );
        User mockUser = mock(User.class);
        MuscleGroup mockMuscleGroup = mock(MuscleGroup.class);
        Exercise mockExercise = mock(Exercise.class);
        Exercise savedExercise = mock(Exercise.class);

        when(securityUtils.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.isSuperAdmin()).thenReturn(true);
        when(mockUser.getId()).thenReturn(99L);

        when(muscleGroupRepository.findById(1L)).thenReturn(Optional.of(mockMuscleGroup));

        when(exerciseRepository.existsBaseExerciseByName("Sentadilla")).thenReturn(false);

        when(exerciseDTOMapper.toDomain(request, true, null, 99L)).thenReturn(mockExercise);
        when(exerciseRepository.save(mockExercise)).thenReturn(savedExercise);
        when(exerciseDTOMapper.toResponse(savedExercise)).thenReturn(mock(ExerciseResponse.class));

        ExerciseResponse response = useCase.execute(request);

        assertNotNull(response);
        verify(exerciseRepository).save(mockExercise);
    }

    @Test
    @DisplayName("Debería crear un ejercicio custom (gym local) exitosamente")
    void shouldCreateCustomExerciseSuccessfully() {
        Long gymId = 10L;
        CreateExerciseRequest request = new CreateExerciseRequest(
                "Remo Custom", "Desc", "img", "vid", false, List.of()
        );
        User mockUser = mock(User.class);
        Exercise mockExercise = mock(Exercise.class);

        when(securityUtils.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.isSuperAdmin()).thenReturn(false); // No es SuperAdmin
        when(securityUtils.getCurrentUserGymId()).thenReturn(gymId);

        doNothing().when(gymValidator).validateExists(gymId);
        when(exerciseRepository.existsByNameAndGymId("Remo Custom", gymId)).thenReturn(false);

        when(mockUser.getId()).thenReturn(99L);
        when(exerciseDTOMapper.toDomain(request, false, gymId, 99L)).thenReturn(mockExercise);
        when(exerciseRepository.save(mockExercise)).thenReturn(mockExercise);
        when(exerciseDTOMapper.toResponse(mockExercise)).thenReturn(mock(ExerciseResponse.class));

        ExerciseResponse response = useCase.execute(request);

        assertNotNull(response);
        verify(exerciseRepository).save(mockExercise);
    }

    @Test
    @DisplayName("Debería lanzar error si el ejercicio base ya existe")
    void shouldThrowExceptionWhenBaseExerciseExists() {
        CreateExerciseRequest request = new CreateExerciseRequest("Sentadilla", "Desc", "img", "vid", true, List.of());
        User mockUser = mock(User.class);

        when(securityUtils.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.isSuperAdmin()).thenReturn(true);
        when(exerciseRepository.existsBaseExerciseByName("Sentadilla")).thenReturn(true);

        assertThrows(BaseExerciseAlreadyExistsException.class, () -> useCase.execute(request));
        verify(exerciseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar error si el ejercicio custom ya existe en el gym")
    void shouldThrowExceptionWhenCustomExerciseExists() {
        Long gymId = 10L;
        CreateExerciseRequest request = new CreateExerciseRequest("Remo Custom", "Desc", "img", "vid", false, List.of());
        User mockUser = mock(User.class);

        when(securityUtils.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.isSuperAdmin()).thenReturn(false);
        when(securityUtils.getCurrentUserGymId()).thenReturn(gymId);
        doNothing().when(gymValidator).validateExists(gymId);
        when(exerciseRepository.existsByNameAndGymId("Remo Custom", gymId)).thenReturn(true);

        assertThrows(GymExerciseAlreadyExistsException.class, () -> useCase.execute(request));
        verify(exerciseRepository, never()).save(any());
    }
}