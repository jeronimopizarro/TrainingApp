package com.trainingapp.trainingapp.application.useCase.tracker;

import com.trainingapp.trainingapp.domain.entity.tracker.TrainingSession;
import com.trainingapp.trainingapp.domain.repository.tracker.TrainingSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CleanZombieSessionsUseCase {

    private static final Logger logger = LoggerFactory.getLogger(CleanZombieSessionsUseCase.class);
    private final TrainingSessionRepository sessionRepository;

    public CleanZombieSessionsUseCase(TrainingSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Transactional
    public void execute() {
        logger.info("Ejecutando caso de uso: Limpieza de sesiones Zombie...");

        // REGLA DE NEGOCIO: 12 horas de antigüedad
        LocalDateTime threshold = LocalDateTime.now().minusHours(12);

        List<TrainingSession> zombieSessions = sessionRepository.findZombieSessions(threshold);

        if (zombieSessions.isEmpty()) {
            logger.info("No se encontraron sesiones Zombie. Todo limpio.");
            return;
        }

        int count = 0;
        for (TrainingSession session : zombieSessions) {
            session.cancel();
            sessionRepository.save(session);
            count++;
        }

        logger.info("Limpieza finalizada. Se cerraron automáticamente {} sesiones Zombie.", count);
    }
}