package com.trainingapp.trainingapp.application.mapper.trainer;

import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.web.dto.user.trainer.RegisterTrainerRequest;
import com.trainingapp.trainingapp.web.dto.user.trainer.TrainerResponse;
import org.springframework.stereotype.Component;

@Component
public class TrainerDTOMapper {

    public Trainer toDomain(RegisterTrainerRequest request, String encodedPassword) {
        if (request == null) return null;
        return new Trainer(
                request.firstName(),
                request.lastName(),
                request.email(),
                encodedPassword,
                request.gymId(),
                request.specialization()
        );
    }

    public TrainerResponse toResponse(Trainer trainer) {
        if (trainer == null) return null;
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