package com.trainingapp.trainingapp.application.useCase.user.trainer;

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

    public DeleteTrainerUseCase(TrainerRepository trainerRepository, SecurityUtils securityUtils) {
        this.trainerRepository = trainerRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public void execute(Long id) {
        Trainer trainer = findTrainerOrThrow(id);

        securityUtils.validateSameGym(trainer.getGymId());

        trainer.deactivate();
        trainerRepository.save(trainer);
    }

    private Trainer findTrainerOrThrow(Long id) {
        return trainerRepository.findById(id)
                .orElseThrow(() -> new TrainerNotFoundException(
                        "Trainer with id " + id + " not found."));
    }
}