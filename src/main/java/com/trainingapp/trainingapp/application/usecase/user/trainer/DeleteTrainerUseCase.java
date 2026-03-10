package com.trainingapp.trainingapp.application.usecase.user.trainer;

import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.domain.exception.user.TrainerNotFoundException;
import com.trainingapp.trainingapp.domain.repository.user.TrainerRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class DeleteTrainerUseCase {

    private final TrainerRepository trainerRepository;

    public DeleteTrainerUseCase(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Transactional
    public void execute(Long id) {
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new TrainerNotFoundException("Trainer with id " + id + " not found."));

        trainer.deactivate();

        trainerRepository.save(trainer);
    }
}