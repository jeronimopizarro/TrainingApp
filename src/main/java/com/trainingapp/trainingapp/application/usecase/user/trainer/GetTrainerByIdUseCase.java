package com.trainingapp.trainingapp.application.usecase.user.trainer;

import com.trainingapp.trainingapp.domain.entity.user.Admin;
import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.domain.exception.user.TrainerNotFoundException;
import com.trainingapp.trainingapp.domain.repository.user.TrainerRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.user.trainer.TrainerResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class GetTrainerByIdUseCase {

    private final TrainerRepository trainerRepository;
    private final SecurityUtils securityUtils;

    public GetTrainerByIdUseCase(TrainerRepository trainerRepository, SecurityUtils securityUtils) {
        this.trainerRepository = trainerRepository;
        this.securityUtils = securityUtils;
    }

    public TrainerResponse execute(Long id) {
        User currentUser = securityUtils.getCurrentUser();

        Trainer trainer = findTrainerOrThrow(id);

        validateAccess(currentUser, trainer);

        return buildResponseFromTrainer(trainer);
    }

    private Trainer findTrainerOrThrow(Long id) {
        return trainerRepository.findById(id)
                .orElseThrow(() -> new TrainerNotFoundException(
                        "Trainer with id " + id + " not found."));
    }

    private void validateAccess(User currentUser, Trainer targetTrainer) {
        if (currentUser.getRole() == Role.TRAINER && !currentUser.getId().equals(targetTrainer.getId())) {
            throw new AccessDeniedException("Solo puedes ver tu propio perfil.");
        }

        if (currentUser.getRole() == Role.MEMBER) {
            Member member = (Member) currentUser;
            if (!member.getGymId().equals(targetTrainer.getGymId())) {
                throw new AccessDeniedException("Solo puedes ver información de los entrenadores de tu gimnasio.");
            }
        }

        if (currentUser.getRole() == Role.GYM_ADMIN) {
            Admin admin = (Admin) currentUser;
            if (!admin.getGymId().equals(targetTrainer.getGymId())) {
                throw new AccessDeniedException("Solo puedes ver información de los entrenadores de tu gimnasio.");
            }
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