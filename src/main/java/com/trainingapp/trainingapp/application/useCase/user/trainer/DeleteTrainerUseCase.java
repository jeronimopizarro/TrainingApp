package com.trainingapp.trainingapp.application.useCase.user.trainer;

import com.trainingapp.trainingapp.application.validator.UserAccessValidator;
import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.domain.exception.user.TrainerNotFoundException;
import com.trainingapp.trainingapp.domain.repository.user.TrainerRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class DeleteTrainerUseCase {

    private final TrainerRepository trainerRepository;
    private final SecurityUtils securityUtils;
    private final UserAccessValidator userAccessValidator;

    public DeleteTrainerUseCase(TrainerRepository trainerRepository, SecurityUtils securityUtils,
                                UserAccessValidator userAccessValidator) {
        this.trainerRepository = trainerRepository;
        this.securityUtils = securityUtils;
        this.userAccessValidator = userAccessValidator;
    }

    @Transactional
    public void execute(Long id) {
        Trainer trainer = findTrainerOrThrow(id);

        securityUtils.validateSameGym(trainer.getGymId());
        userAccessValidator.validateWritePermission(trainer.getId());

        trainer.deactivate();
        trainerRepository.save(trainer);
    }

    private Trainer findTrainerOrThrow(Long id) {
        return trainerRepository.findById(id)
                .orElseThrow(() -> new TrainerNotFoundException(id));
    }
}