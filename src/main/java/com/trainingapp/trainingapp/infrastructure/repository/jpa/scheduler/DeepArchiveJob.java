package com.trainingapp.trainingapp.infrastructure.repository.jpa.scheduler;

import com.trainingapp.trainingapp.application.useCase.user.ArchiveInactiveUsersUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DeepArchiveJob {
    private final ArchiveInactiveUsersUseCase archiveInactiveUsersUseCase;

    public DeepArchiveJob(ArchiveInactiveUsersUseCase archiveInactiveUsersUseCase) {
        this.archiveInactiveUsersUseCase = archiveInactiveUsersUseCase;
    }

    /**
     * Se ejecuta a las 04:00 AM, el día 1 de cada mes.
     * Busca usuarios sin asistencia física por más de 24 meses y los inactiva (Soft-Delete).
     */
    @Scheduled(cron = "0 0 4 1 * ?")
    public void runArchiveJob() {
        archiveInactiveUsersUseCase.execute();
    }
}