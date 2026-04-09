package com.trainingapp.trainingapp.application.useCase.routine;

import com.trainingapp.trainingapp.application.validator.RoutineAccessValidator;
import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompleteRoutineUseCaseTest {

    @Mock private RoutineRepository routineRepository;
    @Mock private RoutineAccessValidator accessValidator;

    @InjectMocks private CompleteRoutineUseCase useCase;

    @Test
    @DisplayName("Debería completar una rutina exitosamente")
    void shouldCompleteRoutineSuccessfully() {
        Routine mockRoutine = mock(Routine.class);

        when(routineRepository.findById(1L)).thenReturn(Optional.of(mockRoutine));
        doNothing().when(accessValidator).validateModificationPermission(mockRoutine);

        useCase.execute(1L);

        verify(mockRoutine).complete();
        verify(routineRepository).save(mockRoutine);
    }
}