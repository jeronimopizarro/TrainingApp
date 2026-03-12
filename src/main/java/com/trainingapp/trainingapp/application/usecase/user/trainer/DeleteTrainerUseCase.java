package com.trainingapp.trainingapp.application.usecase.user.trainer;

import com.trainingapp.trainingapp.domain.entity.user.Admin;
import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.domain.exception.user.TrainerNotFoundException;
import com.trainingapp.trainingapp.domain.repository.user.TrainerRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class DeleteTrainerUseCase {

    private final TrainerRepository trainerRepository;
    private final SecurityUtils securityUtils;

    public DeleteTrainerUseCase(TrainerRepository trainerRepository, SecurityUtils securityUtils) {
        this.trainerRepository = trainerRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public void execute(Long id) {
        User currentUser = securityUtils.getCurrentUser();

        Trainer trainer = findTrainerOrThrow(id);

        validateDeleteAccess(currentUser, trainer);

        trainer.deactivate();

        trainerRepository.save(trainer);
    }

    private Trainer findTrainerOrThrow(Long id) {
        return trainerRepository.findById(id)
                .orElseThrow(() -> new TrainerNotFoundException(
                        "Trainer with id " + id + " not found."));
    }

    private void validateDeleteAccess(User currentUser, Trainer targetTrainer) {
        if (currentUser.getRole() == Role.GYM_ADMIN) {
            Admin admin = (Admin) currentUser;
            if (!admin.getGymId().equals(targetTrainer.getGymId())) {
                throw new AccessDeniedException("Solo puedes dar de baja a entrenadores de tu gimnasio.");
            }
        }
    }
}