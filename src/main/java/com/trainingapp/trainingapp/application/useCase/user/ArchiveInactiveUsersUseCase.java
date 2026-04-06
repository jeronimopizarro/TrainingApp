package com.trainingapp.trainingapp.application.useCase.user;

import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.repository.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public class ArchiveInactiveUsersUseCase {
    private static final Logger logger = LoggerFactory.getLogger(ArchiveInactiveUsersUseCase.class);
    private final UserRepository userRepository;

    public ArchiveInactiveUsersUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void execute() {
        logger.info("Iniciando archivado profundo basado en presencia física...");

        // REGLA DE NEGOCIO: 24 meses sin pasar por el gimnasio
        LocalDateTime threshold = LocalDateTime.now().minusMonths(24);

        List<User> inactiveUsers = userRepository.findUsersWithoutAccessSince(threshold);

        if (inactiveUsers.isEmpty()) {
            logger.info("No hay usuarios inactivos por falta de asistencia en los últimos 24 meses.");
            return;
        }

        int count = 0;
        for (User user : inactiveUsers) {
            user.deactivate();
            userRepository.save(user);
            count++;
        }

        logger.info("Archivado profundo finalizado. Se aplicó soft-delete a {} usuarios fantasmas.", count);
    }
}