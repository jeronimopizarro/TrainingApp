package com.trainingapp.trainingapp.application.usecase.user.trainer;

import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.domain.repository.user.TrainerRepository;
import com.trainingapp.trainingapp.web.dto.user.trainer.RegisterTrainerRequest;
import com.trainingapp.trainingapp.web.dto.user.trainer.TrainerResponse;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterTrainerUseCase {

    private final TrainerRepository trainerRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterTrainerUseCase(TrainerRepository trainerRepository,
                                  PasswordEncoder passwordEncoder) {
        this.trainerRepository = trainerRepository;
        this.passwordEncoder = passwordEncoder;
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
                passwordEncoder.encode(request.password()),
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