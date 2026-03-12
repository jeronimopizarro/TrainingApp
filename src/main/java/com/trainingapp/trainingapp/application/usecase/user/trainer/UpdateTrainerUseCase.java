package com.trainingapp.trainingapp.application.usecase.user.trainer;

import com.trainingapp.trainingapp.domain.entity.user.Admin;
import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.domain.exception.user.TrainerNotFoundException;
import com.trainingapp.trainingapp.domain.repository.user.TrainerRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.user.trainer.TrainerResponse;
import com.trainingapp.trainingapp.web.dto.user.trainer.UpdateTrainerRequest;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class UpdateTrainerUseCase {

    private final TrainerRepository trainerRepository;
    private final SecurityUtils securityUtils;

    public UpdateTrainerUseCase(TrainerRepository trainerRepository, SecurityUtils securityUtils) {
        this.trainerRepository = trainerRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public TrainerResponse execute(Long id, UpdateTrainerRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        Trainer trainer = findTrainerOrThrow(id);

        validateUpdateAccess(currentUser, trainer);

        updateTrainerFields(trainer, request);

        Trainer updatedTrainer = trainerRepository.save(trainer);

        return buildResponseFromTrainer(updatedTrainer);
    }

    private Trainer findTrainerOrThrow(Long id) {
        return trainerRepository.findById(id)
                .orElseThrow(() -> new TrainerNotFoundException(
                        "Trainer with id " + id + " not found."));
    }

    private void validateUpdateAccess(User currentUser, Trainer targetTrainer) {
        if (currentUser.getRole() == Role.TRAINER && !currentUser.getId().equals(targetTrainer.getId())) {
            throw new AccessDeniedException("Solo puedes modificar tu propio perfil.");
        }

        if (currentUser.getRole() == Role.GYM_ADMIN) {
            Admin admin = (Admin) currentUser;
            if (!admin.getGymId().equals(targetTrainer.getGymId())) {
                throw new AccessDeniedException("Solo puedes modificar el perfil de los entrenadores de tu gimnasio.");
            }
        }
    }

    private void updateTrainerFields(Trainer trainer, UpdateTrainerRequest request) {
        if (request.firstName() != null && !request.firstName().isBlank()) {
            trainer.setFirstName(request.firstName());
        }
        if (request.lastName() != null && !request.lastName().isBlank()) {
            trainer.setLastName(request.lastName());
        }
        if (request.specialization() != null) {
            trainer.updateSpecialization(request.specialization());
        }
    }

    private TrainerResponse buildResponseFromTrainer(Trainer trainer) {
        return new TrainerResponse(
                trainer.getId(),
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.getEmail(),
                trainer.getGymId(),
                trainer.getSpecialization(),
                trainer.isActive()
        );
    }
}