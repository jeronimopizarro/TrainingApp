package com.trainingapp.trainingapp.application.useCase.routine;

import com.trainingapp.trainingapp.application.mapper.routine.RoutineDTOMapper;
import com.trainingapp.trainingapp.application.validator.RoutineAccessValidator;
import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.routine.CreateRoutineResponse;
import com.trainingapp.trainingapp.web.dto.routine.DuplicateRoutineRequest;
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
class DuplicateRoutineUseCaseTest {

    @Mock private RoutineRepository routineRepository;
    @Mock private SecurityUtils securityUtils;
    @Mock private RoutineAccessValidator accessValidator;
    @Mock private RoutineDTOMapper routineDTOMapper;

    @InjectMocks private DuplicateRoutineUseCase useCase;

    @Test
    @DisplayName("Debería duplicar una rutina exitosamente")
    void shouldDuplicateRoutineSuccessfully() {
        DuplicateRoutineRequest request = new DuplicateRoutineRequest("Rutina Duplicada", 100L, 2L, 2L);
        Routine sourceRoutine = mock(Routine.class);
        Routine duplicatedRoutine = mock(Routine.class);
        User mockUser = mock(User.class);

        // Obtener usuario actual
        when(securityUtils.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getId()).thenReturn(5L); // Creador

        // Buscar rutina origen
        when(routineRepository.findById(1L)).thenReturn(Optional.of(sourceRoutine));
        when(sourceRoutine.getGymId()).thenReturn(10L);

        // Validaciones
        doNothing().when(securityUtils).validateSameGym(10L);
        doNothing().when(accessValidator).validateTargetMemberAccess(100L);
        doNothing().when(accessValidator).validateTargetTrainerAccess(2L);

        when(sourceRoutine.duplicate("Rutina Duplicada", 100L, 2L, 5L)).thenReturn(duplicatedRoutine);
        when(routineRepository.save(duplicatedRoutine)).thenReturn(duplicatedRoutine);
        when(routineDTOMapper.toResponse(duplicatedRoutine, "Routine duplicated successfully"))
                .thenReturn(mock(CreateRoutineResponse.class));

        CreateRoutineResponse response = useCase.execute(1L, request);

        assertNotNull(response);
        verify(routineRepository).save(duplicatedRoutine);
    }
}