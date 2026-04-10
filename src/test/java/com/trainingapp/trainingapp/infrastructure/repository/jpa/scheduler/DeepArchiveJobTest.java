package com.trainingapp.trainingapp.infrastructure.repository.jpa.scheduler;

import com.trainingapp.trainingapp.application.useCase.user.ArchiveInactiveUsersUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeepArchiveJobTest {

    @Mock
    private ArchiveInactiveUsersUseCase archiveInactiveUsersUseCase;

    @InjectMocks
    private DeepArchiveJob deepArchiveJob;

    @Test
    @DisplayName("Debería ejecutar el caso de uso de archivo profundo")
    void shouldExecuteArchiveUseCase() {
        deepArchiveJob.runArchiveJob();

        verify(archiveInactiveUsersUseCase, times(1)).execute();
    }
}