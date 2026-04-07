package com.trainingapp.trainingapp.application.useCase.tracker;

import com.trainingapp.trainingapp.domain.entity.tracker.TrainingSession;
import com.trainingapp.trainingapp.domain.repository.tracker.TrainingSessionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CleanZombieSessionsUseCaseTest {

    @Mock
    private TrainingSessionRepository sessionRepository;

    @InjectMocks
    private CleanZombieSessionsUseCase useCase;

    @Test
    @DisplayName("Debería cancelar y guardar en la BD si encuentra sesiones zombie")
    void shouldCancelAndSaveZombieSessions() {
        TrainingSession mockSession1 = mock(TrainingSession.class);
        TrainingSession mockSession2 = mock(TrainingSession.class);
        List<TrainingSession> zombies = List.of(mockSession1, mockSession2);

        when(sessionRepository.findZombieSessions(any(LocalDateTime.class))).thenReturn(zombies);

        useCase.execute();

        verify(mockSession1, times(1)).cancel();
        verify(mockSession2, times(1)).cancel();

        verify(sessionRepository, times(2)).save(any(TrainingSession.class));
    }

    @Test
    @DisplayName("No debería hacer nada si la lista de zombies está vacía")
    void shouldDoNothingWhenNoZombieSessions() {
        when(sessionRepository.findZombieSessions(any(LocalDateTime.class))).thenReturn(Collections.emptyList());

        useCase.execute();

        verify(sessionRepository, never()).save(any(TrainingSession.class));
    }
}
