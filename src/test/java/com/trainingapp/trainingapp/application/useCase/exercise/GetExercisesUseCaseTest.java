package com.trainingapp.trainingapp.application.useCase.exercise;

import com.trainingapp.trainingapp.application.mapper.exercise.ExerciseDTOMapper;
import com.trainingapp.trainingapp.application.validator.GymValidator;
import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.exercise.ExerciseDetailResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetExercisesUseCaseTest {

    @Mock private ExerciseRepository exerciseRepository;
    @Mock private SecurityUtils securityUtils;
    @Mock private ExerciseDTOMapper exerciseDTOMapper;
    @Mock private GymValidator gymValidator;

    @InjectMocks private GetExercisesUseCase useCase;

    @Test
    @DisplayName("Debería retornar todos los ejercicios filtrados por grupo muscular para SuperAdmin")
    void shouldReturnAllExercisesForSuperAdmin() {
        Long muscleGroupId = 1L;
        User mockUser = mock(User.class);
        Exercise mockExercise = mock(Exercise.class);

        when(securityUtils.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.isSuperAdmin()).thenReturn(true);

        when(exerciseRepository.findByMuscleGroupId(muscleGroupId)).thenReturn(List.of(mockExercise));
        when(exerciseDTOMapper.toDetailResponse(mockExercise)).thenReturn(mock(ExerciseDetailResponse.class));

        List<ExerciseDetailResponse> result = useCase.execute(muscleGroupId);

        assertEquals(1, result.size());
        verify(exerciseRepository).findByMuscleGroupId(muscleGroupId);
    }

    @Test
    @DisplayName("Debería retornar ejercicios permitidos para un usuario de un gimnasio específico")
    void shouldReturnAllowedExercisesForRegularUser() {
        Long muscleGroupId = 1L;
        Long gymId = 10L;
        User mockUser = mock(User.class);
        Exercise mockExercise = mock(Exercise.class);

        when(securityUtils.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.isSuperAdmin()).thenReturn(false);
        when(securityUtils.getCurrentUserGymId()).thenReturn(gymId);

        doNothing().when(gymValidator).validateExists(gymId);

        when(exerciseRepository.findAllowedForGym(gymId, muscleGroupId)).thenReturn(List.of(mockExercise));
        when(exerciseDTOMapper.toDetailResponse(mockExercise)).thenReturn(mock(ExerciseDetailResponse.class));

        List<ExerciseDetailResponse> result = useCase.execute(muscleGroupId);

        assertEquals(1, result.size());
        verify(exerciseRepository).findAllowedForGym(gymId, muscleGroupId);
    }
}