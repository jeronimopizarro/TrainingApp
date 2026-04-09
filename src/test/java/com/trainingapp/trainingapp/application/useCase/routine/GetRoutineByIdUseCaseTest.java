package com.trainingapp.trainingapp.application.useCase.routine;

import com.trainingapp.trainingapp.application.mapper.routine.RoutineDTOMapper;
import com.trainingapp.trainingapp.application.validator.RoutineAccessValidator;
import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.web.dto.routine.RoutineDetailResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetRoutineByIdUseCaseTest {

    @Mock private RoutineRepository routineRepository;
    @Mock private ExerciseRepository exerciseRepository;
    @Mock private RoutineAccessValidator accessValidator;
    @Mock private RoutineDTOMapper routineDTOMapper;

    @InjectMocks private GetRoutineByIdUseCase useCase;

    @Test
    @DisplayName("Debería retornar una rutina por ID si tiene acceso")
    void shouldReturnRoutineById() {
        Routine mockRoutine = mock(Routine.class);

        when(routineRepository.findById(1L)).thenReturn(Optional.of(mockRoutine));

        doNothing().when(accessValidator).validateReadPermission(mockRoutine);

        when(mockRoutine.getDays()).thenReturn(List.of());
        when(exerciseRepository.findAllById(List.of())).thenReturn(List.of());

        when(routineDTOMapper.toRoutineDetailResponse(mockRoutine, List.of()))
                .thenReturn(mock(RoutineDetailResponse.class));

        RoutineDetailResponse response = useCase.execute(1L);

        assertNotNull(response);
    }
}