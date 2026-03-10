package com.trainingapp.trainingapp.application.usecase.user.trainer;

import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.domain.exception.user.TrainerNotFoundException;
import com.trainingapp.trainingapp.domain.repository.user.TrainerRepository;
import com.trainingapp.trainingapp.web.dto.user.trainer.TrainerResponse;
import org.springframework.stereotype.Service;

@Service
public class GetTrainerByIdUseCase {

    private final TrainerRepository trainerRepository;

    public GetTrainerByIdUseCase(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    public TrainerResponse execute(Long id) {
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new TrainerNotFoundException("Trainer with id " + id + " not found."));

        return buildResponseFromTrainer(trainer);
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