package com.trainingapp.trainingapp.application.usecase.user.trainer;

import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.domain.exception.user.TrainerNotFoundException;
import com.trainingapp.trainingapp.domain.repository.user.TrainerRepository;
import com.trainingapp.trainingapp.web.dto.user.trainer.TrainerResponse;
import com.trainingapp.trainingapp.web.dto.user.trainer.UpdateTrainerRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UpdateTrainerUseCase {

    private final TrainerRepository trainerRepository;

    public UpdateTrainerUseCase(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Transactional
    public TrainerResponse execute(Long id, UpdateTrainerRequest request) {
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new TrainerNotFoundException("Trainer with id " + id + " not found."));

        updateTrainerFields(trainer, request);

        Trainer updatedTrainer = trainerRepository.save(trainer);

        return buildResponseFromTrainer(updatedTrainer);
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