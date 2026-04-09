package com.trainingapp.trainingapp.application.useCase.routine;

import com.trainingapp.trainingapp.application.mapper.routine.RoutineDTOMapper;
import com.trainingapp.trainingapp.application.validator.RoutineAccessValidator;
import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.routine.RoutineStatus;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.routine.CreateRoutineResponse;
import com.trainingapp.trainingapp.web.dto.routine.UpdateRoutineRequest;
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
class UpdateRoutineUseCaseTest {

    @Mock private RoutineRepository routineRepository;
    @Mock private ExerciseRepository exerciseRepository;
    @Mock private SecurityUtils securityUtils;
    @Mock private RoutineAccessValidator accessValidator;
    @Mock private RoutineDTOMapper routineDTOMapper;

    @InjectMocks private UpdateRoutineUseCase useCase;

    @Test
    @DisplayName("Debería actualizar los detalles de la rutina y guardarla")
    void shouldUpdateRoutineSuccessfully() {
        UpdateRoutineRequest request = new UpdateRoutineRequest("Fuerza Plus", 2L, List.of());
        Routine mockRoutine = mock(Routine.class);
        User mockUser = mock(User.class);

        when(routineRepository.findById(1L)).thenReturn(Optional.of(mockRoutine));
        doNothing().when(accessValidator).validateModificationPermission(mockRoutine);
        when(mockRoutine.getStatus()).thenReturn(RoutineStatus.DRAFT);

        when(securityUtils.getCurrentUserGymId()).thenReturn(10L);
        when(securityUtils.getCurrentUser()).thenReturn(mockUser);

        when(routineDTOMapper.toDomainDays(List.of())).thenReturn(List.of());
        when(routineRepository.save(mockRoutine)).thenReturn(mockRoutine);
        when(routineDTOMapper.toResponse(mockRoutine, "Routine updated successfully"))
                .thenReturn(mock(CreateRoutineResponse.class));

        CreateRoutineResponse response = useCase.execute(1L, request);

        assertNotNull(response);
        verify(mockRoutine).update("Fuerza Plus", 2L, List.of());
        verify(routineRepository).save(mockRoutine);
    }
}