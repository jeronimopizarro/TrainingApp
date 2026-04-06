package com.trainingapp.trainingapp.application.useCase.user.trainer;

import com.trainingapp.trainingapp.application.mapper.trainer.TrainerDTOMapper;
import com.trainingapp.trainingapp.application.validator.UserAccessValidator;
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
    private final UserAccessValidator userAccessValidator;

    public GetTrainerByIdUseCase(TrainerRepository trainerRepository, SecurityUtils securityUtils,
                                 TrainerDTOMapper trainerDTOMapper,
                                 UserAccessValidator userAccessValidator) {
        this.trainerRepository = trainerRepository;
        this.securityUtils = securityUtils;
        this.trainerDTOMapper = trainerDTOMapper;
        this.userAccessValidator = userAccessValidator;
    }

    public TrainerResponse execute(Long id) {
        Trainer trainer = findTrainerOrThrow(id);

        securityUtils.validateSameGym(trainer.getGymId());
        userAccessValidator.validateReadPermission(trainer);

        return trainerDTOMapper.toResponse(trainer);
    }

    private Trainer findTrainerOrThrow(Long id) {
        return trainerRepository.findById(id)
                .orElseThrow(() -> new TrainerNotFoundException(id));
    }
}
