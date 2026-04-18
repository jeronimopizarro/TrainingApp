package com.trainingapp.trainingapp.application.useCase.tracker;

import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.domain.entity.tracker.TrainingSession;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import com.trainingapp.trainingapp.domain.repository.tracker.TrainingSessionRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.tracker.ExerciseProgressResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetExerciseProgressUseCaseTest {

    @Mock private SecurityUtils securityUtils;
    @Mock private TrainingSessionRepository trainingSessionRepository;
    @Mock private ExerciseRepository exerciseRepository;

    @InjectMocks private GetExerciseProgressUseCase useCase;

    @Test
    @DisplayName("Debería calcular el progreso de un ejercicio a lo largo del tiempo")
    void shouldReturnExerciseProgress() {
        // Simulamos la obtención del usuario desde el JWT
        User mockUser = mock(User.class);
        when(securityUtils.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getId()).thenReturn(100L);

        // Simulamos el ejercicio
        Exercise mockExercise = mock(Exercise.class);
        when(exerciseRepository.findById(5L)).thenReturn(Optional.of(mockExercise));
        when(mockExercise.getId()).thenReturn(5L);
        when(mockExercise.getName()).thenReturn("Sentadilla");

        // Simulamos las sesiones
        TrainingSession mockSession = mock(TrainingSession.class);
        when(trainingSessionRepository.findSessionsByMemberAndExercise(eq(100L), eq(5L), any()))
                .thenReturn(List.of(mockSession));

        when(mockSession.calculateAverageE1RMForExercise(5L)).thenReturn(new BigDecimal("120.0"));
        when(mockSession.getStartTime()).thenReturn(LocalDateTime.now());

        ExerciseProgressResponse response = useCase.execute(5L, null, 6);

        assertNotNull(response);
        assertEquals("Sentadilla", response.exerciseName());
        assertEquals(1, response.dataPoints().size());
    }
}