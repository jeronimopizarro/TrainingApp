package com.trainingapp.trainingapp.application.usecase.user.trainer;

import com.trainingapp.trainingapp.domain.entity.user.Admin;
import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.domain.repository.user.TrainerRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.user.trainer.TrainerResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllTrainersByGymIdUseCase {

    private final TrainerRepository trainerRepository;
    private final SecurityUtils  securityUtils;

    public GetAllTrainersByGymIdUseCase(TrainerRepository trainerRepository,
                                        SecurityUtils securityUtils) {
        this.trainerRepository = trainerRepository;
        this.securityUtils = securityUtils;
    }

    public List<TrainerResponse> execute(Long gymId) {
        User currentUser = securityUtils.getCurrentUser();

        validateGymAccess(currentUser, gymId);

        List<Trainer> trainers = trainerRepository.findByGymId(gymId);

        return mapToResponseList(trainers);
    }

    private void validateGymAccess(User currentUser, Long targetGymId) {
        if (currentUser.getRole() == Role.MEMBER) {
            Member member = (Member) currentUser;
            if (!member.getGymId().equals(targetGymId)) {
                throw new AccessDeniedException("Solo puedes listar a los entrenadores de tu propio gimnasio.");
            }
        }

        if (currentUser.getRole() == Role.GYM_ADMIN) {
            Admin admin = (Admin) currentUser;
            if (!admin.getGymId().equals(targetGymId)) {
                throw new AccessDeniedException("Solo puedes listar a los entrenadores de tu propio gimnasio.");
            }
        }
    }

    private List<TrainerResponse> mapToResponseList(List<Trainer> trainers) {
        return trainers.stream()
                .map(this::buildResponseFromTrainer)
                .toList();
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