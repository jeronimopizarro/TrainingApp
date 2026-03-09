package com.trainingapp.trainingapp.application.usecase.user;

import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.domain.repository.user.TrainerRepository;
import com.trainingapp.trainingapp.web.dto.user.RegisterTrainerRequest;
import com.trainingapp.trainingapp.web.dto.user.TrainerResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class RegisterTrainerUseCase {

    private final TrainerRepository trainerRepository;

    public RegisterTrainerUseCase(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Transactional
    public TrainerResponse execute(RegisterTrainerRequest request) {
        Trainer trainer = buildTrainerFromRequest(request);

        Trainer savedTrainer = trainerRepository.save(trainer);

        return buildResponseFromTrainer(savedTrainer);
    }


    private Trainer buildTrainerFromRequest(RegisterTrainerRequest request) {
        return new Trainer(
                request.firstName(),
                request.lastName(),
                request.email(),
                request.password(),
                request.gymId(),
                request.specialization()
        );
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