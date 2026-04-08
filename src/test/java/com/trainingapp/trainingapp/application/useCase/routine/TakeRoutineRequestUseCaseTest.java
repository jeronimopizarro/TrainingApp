package com.trainingapp.trainingapp.application.useCase.routine;

import com.trainingapp.trainingapp.domain.entity.routine.RoutineRequest;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.routine.ExperienceLevel;
import com.trainingapp.trainingapp.domain.enums.routine.RoutineRequestStatus;
import com.trainingapp.trainingapp.domain.exception.routine.RoutineRequestNotFoundException;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRequestRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TakeRoutineRequestUseCaseTest {

    @Mock
    private RoutineRequestRepository routineRequestRepository;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private TakeRoutineRequestUseCase useCase;

    private RoutineRequest createRestoredRequest(RoutineRequestStatus status) {
        return RoutineRequest.restore(
                1L, 100L, 10L, LocalDateTime.now(),
                status, null, null, null, 3, ExperienceLevel.BEGINNER, "Ninguna", "Hipertrofia"
        );
    }

    @Test
    @DisplayName("Debería asignar el entrenador y pasar a IN_PROGRESS si la solicitud está PENDING")
    void shouldAssignTrainerAndSaveRequest_WhenRequestIsPending() {
        // Arrange: Simulamos un profe logueado con ID 2L
        User mockTrainer = mock(User.class);
        when(mockTrainer.getId()).thenReturn(2L);
        when(securityUtils.getCurrentUser()).thenReturn(mockTrainer);

        // Simulamos una solicitud PENDING en la base de datos (Gym ID 10L)
        RoutineRequest pendingRequest = createRestoredRequest(RoutineRequestStatus.PENDING);
        when(routineRequestRepository.findById(1L)).thenReturn(Optional.of(pendingRequest));

        // El validador multi-tenant no hace nada (void) si está todo ok
        doNothing().when(securityUtils).validateSameGym(10L);

        // El profe toma la solicitud
        useCase.execute(1L);

        assertEquals(RoutineRequestStatus.IN_PROGRESS, pendingRequest.getStatus(), "El estado debe cambiar a IN_PROGRESS");
        assertEquals(2L, pendingRequest.getAssignedTrainerId(), "Debe asignar el ID del profe logueado");
        verify(routineRequestRepository).save(pendingRequest);
    }

    @Test
    @DisplayName("Debería lanzar error si la solicitud no existe en la base de datos")
    void shouldThrowException_WhenRequestNotFound() {
        User mockTrainer = mock(User.class);
        when(mockTrainer.getId()).thenReturn(2L);
        when(securityUtils.getCurrentUser()).thenReturn(mockTrainer);

        when(routineRequestRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RoutineRequestNotFoundException.class, () -> useCase.execute(99L));

        verify(routineRequestRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar error de negocio si la solicitud ya fue tomada (No está PENDING)")
    void shouldThrowException_WhenRequestIsNotPending() {
        User mockTrainer = mock(User.class);
        when(mockTrainer.getId()).thenReturn(2L);
        when(securityUtils.getCurrentUser()).thenReturn(mockTrainer);

        // Simulamos una solicitud que ALGUIEN MÁS ya tomó (está IN_PROGRESS)
        RoutineRequest inProgressRequest = createRestoredRequest(RoutineRequestStatus.IN_PROGRESS);
        when(routineRequestRepository.findById(1L)).thenReturn(Optional.of(inProgressRequest));

        doNothing().when(securityUtils).validateSameGym(10L);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> useCase.execute(1L));
        assertEquals("Solo se pueden asignar solicitudes PENDING", exception.getMessage());
    }
}