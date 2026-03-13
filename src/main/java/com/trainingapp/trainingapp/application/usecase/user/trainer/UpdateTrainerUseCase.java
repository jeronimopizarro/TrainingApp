package com.trainingapp.trainingapp.application.usecase.user.trainer;

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
        Trainer trainer = findTrainerOrThrow(id);
        User currentUser = securityUtils.getCurrentUser();

        securityUtils.validateSameGym(trainer.getGymId());

        if (currentUser.getRole() == Role.TRAINER && !currentUser.getId().equals(trainer.getId())) {
            throw new AccessDeniedException("Solo puedes modificar tu propio perfil.");
        }

        trainer.updateProfile(request.firstName(), request.lastName(), request.specialization());
        Trainer updatedTrainer = trainerRepository.save(trainer);

        return buildResponseFromTrainer(updatedTrainer);
    }

    private Trainer findTrainerOrThrow(Long id) {
        return trainerRepository.findById(id)
                .orElseThrow(() -> new TrainerNotFoundException(
                        "Trainer with id " + id + " not found."));
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