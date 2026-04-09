package com.trainingapp.trainingapp.application.useCase.routine;

import com.trainingapp.trainingapp.application.mapper.routine.RoutineDTOMapper;
import com.trainingapp.trainingapp.application.validator.RoutineAccessValidator;
import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.web.dto.routine.ActivateRoutineRequest;
import com.trainingapp.trainingapp.web.dto.routine.RoutineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivateRoutineUseCaseTest {

    @Mock private RoutineRepository routineRepository;
    @Mock private RoutineAccessValidator accessValidator;
    @Mock private RoutineDTOMapper routineDTOMapper;

    @InjectMocks private ActivateRoutineUseCase useCase;

    @Test
    @DisplayName("Debería activar una rutina exitosamente")
    void shouldActivateRoutineSuccessfully() {
        ActivateRoutineRequest request = new ActivateRoutineRequest(2L, LocalDate.now(), LocalDate.now().plusMonths(1));
        Routine mockRoutine = mock(Routine.class);

        when(routineRepository.findById(1L)).thenReturn(Optional.of(mockRoutine));
        doNothing().when(accessValidator).validateModificationPermission(mockRoutine);

        when(routineRepository.save(mockRoutine)).thenReturn(mockRoutine);
        when(routineDTOMapper.toResponse(mockRoutine)).thenReturn(mock(RoutineResponse.class));

        RoutineResponse response = useCase.execute(1L, request);

        assertNotNull(response);
        verify(mockRoutine).activate(request.startDate(), request.endDate());
        verify(routineRepository).save(mockRoutine);
    }
}