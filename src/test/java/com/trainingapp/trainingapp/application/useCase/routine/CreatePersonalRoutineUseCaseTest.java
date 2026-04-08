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
import com.trainingapp.trainingapp.web.dto.routine.CreatePersonalRoutineRequest;
import com.trainingapp.trainingapp.web.dto.routine.CreateRoutineResponse;
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
public class CreatePersonalRoutineUseCaseTest {

    @Mock private SecurityUtils securityUtils;
    @Mock private RoutineRepository routineRepository;
    @Mock private RoutineRequestRepository requestRepository;
    @Mock private ExerciseRepository exerciseRepository;
    @Mock private RoutineDTOMapper mapper;
    @Mock private GymValidator gymValidator;

    @InjectMocks
    private CreatePersonalRoutineUseCase useCase;

    @Test
    @DisplayName("Debería crear rutina personal y completar la solicitud IN_PROGRESS automáticamente")
    void shouldCreatePersonalRoutineAndCompletePendingRequest() {
        User mockMember = mock(User.class);
        when(mockMember.getId()).thenReturn(100L);
        when(securityUtils.getCurrentUser()).thenReturn(mockMember);
        when(securityUtils.getCurrentUserGymId()).thenReturn(10L);

        // Simulamos la request de creación (usamos List.of() para evitar NullPointerExceptions en validaciones)
        CreatePersonalRoutineRequest request = new CreatePersonalRoutineRequest("Mi Propia Rutina", List.of());

        Routine mockRoutine = Routine.restore(50L, "Mi Propia Rutina", null, null, 100L, 100L, 100L, 10L, RoutineStatus.DRAFT, true, List.of());
        CreateRoutineResponse mockResponse = new CreateRoutineResponse(50L, "Rutina personal creada con éxito.");

        when(mapper.toDomain(any(CreatePersonalRoutineRequest.class), eq(100L), eq(10L))).thenReturn(mockRoutine);
        when(routineRepository.save(any(Routine.class))).thenReturn(mockRoutine);
        when(mapper.toResponse(eq(mockRoutine), anyString())).thenReturn(mockResponse);


        RoutineRequest pendingRequest = RoutineRequest.restore(
                1L, 100L, 10L, LocalDateTime.now(), RoutineRequestStatus.PENDING, null, null, null, 3, ExperienceLevel.BEGINNER, "Ninguna", "Hipertrofia"
        );

        when(requestRepository.findFirstByMemberIdAndStatus(100L, RoutineRequestStatus.PENDING))
                .thenReturn(Optional.of(pendingRequest));

        // El alumno ejecuta la creación de su propia rutina
        CreateRoutineResponse response = useCase.execute(request);

        assertEquals(50L, response.id(), "Debe retornar el ID de la rutina creada");
        assertEquals("Rutina personal creada con éxito.", response.message());

        // Verificamos que el Request original se CANCELÓ porque el alumno se hizo la rutina solo
        assertEquals(RoutineRequestStatus.CANCELLED, pendingRequest.getStatus(), "La solicitud debió cambiar a CANCELLED");
        verify(requestRepository).save(pendingRequest); // Se guardó la cancelación en la BD
    }
}
