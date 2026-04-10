package com.trainingapp.trainingapp.infrastructure.repository.jpa.scheduler;

import com.trainingapp.trainingapp.application.useCase.tracker.CleanZombieSessionsUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ZombieSessionCleanerJobTest {

    @Mock
    private CleanZombieSessionsUseCase cleanZombieSessionsUseCase;

    @InjectMocks
    private ZombieSessionCleanerJob zombieSessionCleanerJob;

    @Test
    @DisplayName("Debería ejecutar el caso de uso de limpieza de sesiones zombi")
    void shouldExecuteCleanZombieSessionsUseCase() {
        zombieSessionCleanerJob.runCleanerJob();

        verify(cleanZombieSessionsUseCase, times(1)).execute();
    }
}