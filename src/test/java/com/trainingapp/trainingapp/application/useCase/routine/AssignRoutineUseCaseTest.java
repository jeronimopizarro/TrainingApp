package com.trainingapp.trainingapp.application.useCase.routine;

import com.trainingapp.trainingapp.application.mapper.routine.RoutineDTOMapper;
import com.trainingapp.trainingapp.application.validator.GymValidator;
import com.trainingapp.trainingapp.application.validator.MemberAccessValidator;
import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.entity.routine.RoutineRequest;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.routine.ExperienceLevel;
import com.trainingapp.trainingapp.domain.enums.routine.RoutineRequestStatus;
import com.trainingapp.trainingapp.domain.enums.routine.RoutineStatus;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRequestRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.routine.AssignRoutineRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AssignRoutineUseCaseTest {

    @Mock
    private SecurityUtils securityUtils;
    @Mock
    private RoutineRepository routineRepository;
    @Mock
    private RoutineRequestRepository requestRepository;
    @Mock
    private ExerciseRepository exerciseRepository;
    @Mock
    private RoutineDTOMapper mapper;
    @Mock
    private MemberAccessValidator accessValidator;
    @Mock
    private GymValidator gymValidator;

    @InjectMocks
    private AssignRoutineUseCase useCase;

    @Test
    @DisplayName("Al guardar la rutina, debería completar la solicitud IN_PROGRESS del alumno")
    void execute_ShouldCreateRoutineAndCompletePendingRequest() {
        // Arrange: Simulamos que el Profe (ID 2) está logueado
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(2L);
        when(securityUtils.getCurrentUser()).thenReturn(mockUser);
        when(securityUtils.getCurrentUserGymId()).thenReturn(10L);

        // Simulamos que el repositorio devuelve una rutina guardada con ID 50
        Routine savedRoutine = Routine.restore(50L, "Rutina Fuerte", null, null, 100L, 2L, 2L, 10L,
                RoutineStatus.DRAFT, true, List.of());
        when(mapper.toDomain(any(AssignRoutineRequest.class), eq(2L), eq(10L))).thenReturn(savedRoutine);
        when(routineRepository.save(any())).thenReturn(savedRoutine);

        // Acá está la magia: Simulamos que había un Request EN PROGRESO para este alumno y profe
        RoutineRequest inProgressRequest = RoutineRequest.restore(
                1L, 100L, 10L, LocalDateTime.now(), RoutineRequestStatus.IN_PROGRESS, 2L, null,
                null, 3, ExperienceLevel.BEGINNER, "", "Hipertrofia"
        );
        when(requestRepository.findFirstByMemberIdAndStatusAndAssignedTrainerId(100L,
                RoutineRequestStatus.IN_PROGRESS, 2L))
                .thenReturn(Optional.of(inProgressRequest));

        // Act
        // CREAMOS UN RECORD REAL EN LUGAR DE UN MOCK PARA QUE TENGA EL MEMBER_ID 100L
        AssignRoutineRequest request = new AssignRoutineRequest( "Rutina", 100L , List.of());
        useCase.execute(request);

        // Assert
        assertEquals(RoutineRequestStatus.COMPLETED, inProgressRequest.getStatus(),
                "La solicitud debió cambiar a COMPLETED");
        assertEquals(50L, inProgressRequest.getRoutineId(),
                "El request debe guardar el ID de la nueva rutina");
        verify(requestRepository).save(inProgressRequest); // Verificamos que se actualizó en la BD
    }
}
