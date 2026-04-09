package com.trainingapp.trainingapp.application.useCase.routine;

import com.trainingapp.trainingapp.application.mapper.routine.RoutineDTOMapper;
import com.trainingapp.trainingapp.application.validator.RoutineAccessValidator;
import com.trainingapp.trainingapp.domain.entity.routine.Routine;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InactiveRoutineUseCaseTest {

    @Mock private RoutineRepository routineRepository;
    @Mock private RoutineAccessValidator accessValidator;
    @Mock private RoutineDTOMapper routineDTOMapper;

    @InjectMocks private InactiveRoutineUseCase useCase;

    @Test
    @DisplayName("Debería inactivar una rutina exitosamente")
    void shouldInactiveRoutineSuccessfully() {
        Routine mockRoutine = mock(Routine.class);
        when(routineRepository.findById(1L)).thenReturn(Optional.of(mockRoutine));
        doNothing().when(accessValidator).validateModificationPermission(mockRoutine);

        when(routineRepository.save(mockRoutine)).thenReturn(mockRoutine);
        when(routineDTOMapper.toResponse(mockRoutine)).thenReturn(mock(RoutineResponse.class));

        RoutineResponse response = useCase.execute(1L);

        assertNotNull(response);
        verify(mockRoutine).inactive();
        verify(routineRepository).save(mockRoutine);
    }
}