package com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.user;

import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.user.TrainerJpaEntity;
import com.trainingapp.trainingapp.web.dto.user.trainer.RegisterTrainerRequest;
import com.trainingapp.trainingapp.web.dto.user.trainer.TrainerResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TrainerMapper {

    public Trainer toDomain(TrainerJpaEntity entity) {
        if (entity == null) return null;

        return Trainer.restore(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getDni(),
                entity.getRole(),
                entity.isActive(),
                entity.getGymId(),
                entity.getSpecialization()
        );
    }

    public TrainerJpaEntity toEntity(Trainer domain) {
        if (domain == null) return null;

        TrainerJpaEntity entity = new TrainerJpaEntity();
        entity.setId(domain.getId());
        entity.setFirstName(domain.getFirstName());
        entity.setLastName(domain.getLastName());
        entity.setEmail(domain.getEmail());
        entity.setPassword(domain.getPassword());
        entity.setDni(domain.getDni());
        entity.setRole(domain.getRole());
        entity.setActive(domain.isActive());
        entity.setGymId(domain.getGymId());
        entity.setSpecialization(domain.getSpecialization());

        return entity;
    }
}