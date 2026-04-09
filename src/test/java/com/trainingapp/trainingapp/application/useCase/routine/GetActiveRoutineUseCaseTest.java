package com.trainingapp.trainingapp.application.useCase.routine;

import com.trainingapp.trainingapp.application.mapper.routine.RoutineDTOMapper;
import com.trainingapp.trainingapp.application.validator.MemberAccessValidator;
import com.trainingapp.trainingapp.application.validator.RoutineAccessValidator;
import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.enums.routine.RoutineStatus;
import com.trainingapp.trainingapp.domain.exception.routine.RoutineNotFoundException;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.web.dto.routine.RoutineResponse;
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
class GetActiveRoutineUseCaseTest {

    @Mock private RoutineRepository routineRepository;
    @Mock private RoutineDTOMapper routineDTOMapper;
    @Mock private ExerciseRepository exerciseRepository;
    @Mock private RoutineAccessValidator accessValidator;

    @InjectMocks private GetActiveRoutineUseCase useCase;

    @Test
    @DisplayName("Debería retornar la rutina activa del miembro")
    void shouldReturnActiveRoutine() {
        Routine mockRoutine = mock(Routine.class);

        doNothing().when(accessValidator).validateTargetMemberAccess(100L);
        when(routineRepository.findByMemberIdAndStatus(100L, RoutineStatus.ACTIVE))
                .thenReturn(Optional.of(mockRoutine));

        when(routineDTOMapper.toResponse(mockRoutine)).thenReturn(mock(RoutineResponse.class));

        RoutineResponse response = useCase.execute(100L);
        assertNotNull(response);
    }

    @Test
    @DisplayName("Debería lanzar error si no tiene rutina activa")
    void shouldThrowExceptionWhenNoActiveRoutine() {
        doNothing().when(accessValidator).validateTargetMemberAccess(100L);

        when(routineRepository.findByMemberIdAndStatus(100L, RoutineStatus.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(RoutineNotFoundException.class, () -> useCase.execute(100L));
    }
}