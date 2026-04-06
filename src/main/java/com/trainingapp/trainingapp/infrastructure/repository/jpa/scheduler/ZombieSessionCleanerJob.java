package com.trainingapp.trainingapp.infrastructure.repository.jpa.scheduler;

import com.trainingapp.trainingapp.application.useCase.tracker.CleanZombieSessionsUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ZombieSessionCleanerJob {
    private final CleanZombieSessionsUseCase cleanZombieSessionsUseCase;

    public ZombieSessionCleanerJob(CleanZombieSessionsUseCase cleanZombieSessionsUseCase) {
        this.cleanZombieSessionsUseCase = cleanZombieSessionsUseCase;
    }

    /**
     * Se ejecuta todos los días a las 03:00 AM.
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void runCleanerJob() {
        cleanZombieSessionsUseCase.execute();
    }
}
