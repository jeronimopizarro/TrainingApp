package com.trainingapp.trainingapp.application.useCase.user.trainer;

import com.trainingapp.trainingapp.application.mapper.trainer.TrainerDTOMapper;
import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.exception.user.TrainerNotFoundException;
import com.trainingapp.trainingapp.domain.exception.user.UnauthorizedProfileAccessException;
import com.trainingapp.trainingapp.domain.repository.user.TrainerRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.user.trainer.TrainerResponse;
import org.springframework.stereotype.Service;

@Service
public class GetTrainerByIdUseCase {

    private final TrainerRepository trainerRepository;
    private final SecurityUtils securityUtils;
    private final TrainerDTOMapper trainerDTOMapper;

    public GetTrainerByIdUseCase(TrainerRepository trainerRepository, SecurityUtils securityUtils,
                                 TrainerDTOMapper trainerDTOMapper) {
        this.trainerRepository = trainerRepository;
        this.securityUtils = securityUtils;
        this.trainerDTOMapper = trainerDTOMapper;
    }

    public TrainerResponse execute(Long id) {
        Trainer trainer = findTrainerOrThrow(id);
        User currentUser = securityUtils.getCurrentUser();

        securityUtils.validateSameGym(trainer.getGymId());
        validateReadPermission(currentUser, trainer);

        return trainerDTOMapper.toResponse(trainer);
    }

    private Trainer findTrainerOrThrow(Long id) {
        return trainerRepository.findById(id)
                .orElseThrow(() -> new TrainerNotFoundException(id));
    }

    private void validateReadPermission(User currentUser, Trainer targetTrainer) {
        boolean isSuperAdmin = currentUser.isSuperAdmin();
        boolean isAdmin = currentUser.isGymAdmin();
        boolean isSelfTrainer = currentUser.isTrainer() && currentUser.getId()
                .equals(targetTrainer.getId());
        boolean isMemberOfGym = currentUser.isMember();

        if (!isSuperAdmin && !isAdmin && !isSelfTrainer && !isMemberOfGym) {
            throw new UnauthorizedProfileAccessException();
        }
    }
}
