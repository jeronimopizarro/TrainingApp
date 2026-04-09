package com.trainingapp.trainingapp.application.useCase.routine;

import com.trainingapp.trainingapp.application.mapper.routine.RoutineDTOMapper;
import com.trainingapp.trainingapp.application.validator.RoutineAccessValidator;
import com.trainingapp.trainingapp.domain.entity.routine.RoutineSummary;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.web.dto.routine.GetAllRoutinesByTrainerIdResponse;
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
class GetAllRoutinesByTrainerIdUseCaseTest {

    @Mock private RoutineRepository routineRepository;
    @Mock private RoutineDTOMapper routineDTOMapper;
    @Mock private RoutineAccessValidator accessValidator;

    @InjectMocks private GetAllRoutinesByTrainerIdUseCase useCase;

    @Test
    @DisplayName("Debería retornar todas las rutinas creadas por un entrenador")
    void shouldReturnAllRoutinesForTrainer() {
        doNothing().when(accessValidator).validateTargetTrainerAccess(2L);

        RoutineSummary mockSummary = mock(RoutineSummary.class);
        when(routineRepository.findAllSummariesByTrainerId(2L)).thenReturn(List.of(mockSummary));

        when(routineDTOMapper.toAllRoutinesByTrainerIdResponse(mockSummary))
                .thenReturn(mock(GetAllRoutinesByTrainerIdResponse.class));

        List<GetAllRoutinesByTrainerIdResponse> responses = useCase.execute(2L);

        assertEquals(1, responses.size());
    }
}