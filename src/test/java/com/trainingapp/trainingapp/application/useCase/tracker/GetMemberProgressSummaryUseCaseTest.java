package com.trainingapp.trainingapp.application.useCase.tracker;

import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.domain.entity.tracker.TrainingSession;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import com.trainingapp.trainingapp.domain.repository.tracker.TrainingSessionRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.tracker.MemberProgressSummaryResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetMemberProgressSummaryUseCaseTest {

    @Mock private SecurityUtils securityUtils;
    @Mock private TrainingSessionRepository trainingSessionRepository;
    @Mock private ExerciseRepository exerciseRepository;

    @InjectMocks private GetMemberProgressSummaryUseCase useCase;

    @Test
    @DisplayName("Debería retornar un resumen de progreso con los PR de los ejercicios")
    void shouldReturnMemberProgressSummary() {
        // Simulamos la obtención del usuario desde el JWT
        User mockUser = mock(User.class);
        when(securityUtils.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getId()).thenReturn(100L);

        // Simulamos que ha hecho 1 solo ejercicio (ID 5)
        when(trainingSessionRepository.findPerformedExerciseIdsByMemberId(100L)).thenReturn(List.of(5L));

        // Simulamos la búsqueda del ejercicio
        Exercise mockExercise = mock(Exercise.class);
        when(exerciseRepository.findById(5L)).thenReturn(Optional.of(mockExercise));
        when(mockExercise.getName()).thenReturn("Press Banca");

        // Simulamos sus sesiones históricas
        TrainingSession mockSession = mock(TrainingSession.class);
        when(trainingSessionRepository.findSessionsByMemberAndExercise(eq(100L), eq(5L), any()))
                .thenReturn(List.of(mockSession));

        // Simulamos la matemática de la Entidad
        when(mockSession.calculateAverageE1RMForExercise(5L)).thenReturn(new BigDecimal("100.5"));

        MemberProgressSummaryResponse response = useCase.execute(null);

        assertNotNull(response);
        assertEquals(1, response.exercises().size());
        assertEquals("Press Banca", response.exercises().get(0).exerciseName());
        assertEquals(new BigDecimal("100.5"), response.exercises().get(0).currentPersonalRecord());
    }
}