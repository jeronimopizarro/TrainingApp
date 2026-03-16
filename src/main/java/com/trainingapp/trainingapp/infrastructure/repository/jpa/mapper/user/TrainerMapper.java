package com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.user;

import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.user.TrainerJpaEntity;
import com.trainingapp.trainingapp.web.dto.user.trainer.RegisterTrainerRequest;
import com.trainingapp.trainingapp.web.dto.user.trainer.TrainerResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TrainerMapper {

    public TrainerJpaEntity toJpaEntity(Trainer domain) {
        if (domain == null) return null;

        TrainerJpaEntity entity = new TrainerJpaEntity();

        entity.setId(domain.getId());
        entity.setFirstName(domain.getFirstName());
        entity.setLastName(domain.getLastName());
        entity.setEmail(domain.getEmail());
        entity.setPassword(domain.getPassword());
        entity.setRole(domain.getRole());
        entity.setActive(domain.isActive());
        entity.setGymId(domain.getGymId());
        entity.setSpecialization(domain.getSpecialization());

        if (!domain.isActive()) entity.setDeletedAt(LocalDateTime.now());

        return entity;
    }

    public Trainer toDomain(TrainerJpaEntity entity) {
        if (entity == null) return null;

        Trainer trainer = new Trainer(
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getGymId(),
                entity.getSpecialization()
        );

        trainer.setId(entity.getId());
        trainer.setActive(entity.isActive());

        return trainer;
    }

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