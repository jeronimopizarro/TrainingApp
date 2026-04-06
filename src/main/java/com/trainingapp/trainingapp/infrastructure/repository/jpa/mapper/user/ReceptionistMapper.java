package com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.user;

import com.trainingapp.trainingapp.domain.entity.user.Receptionist;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.user.ReceptionistJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ReceptionistMapper {

    public Receptionist toDomain(ReceptionistJpaEntity entity) {
        if (entity == null) return null;
        return Receptionist.restore(
                entity.getId(), entity.getFirstName(), entity.getLastName(),
                entity.getEmail(), entity.getPassword(), entity.getDni(), entity.isActive(),
                entity.getGymId()
        );
    }

    public ReceptionistJpaEntity toEntity(Receptionist domain) {
        if (domain == null) return null;
        ReceptionistJpaEntity entity = new ReceptionistJpaEntity();
        entity.setId(domain.getId());
        entity.setFirstName(domain.getFirstName());
        entity.setLastName(domain.getLastName());
        entity.setEmail(domain.getEmail());
        entity.setPassword(domain.getPassword());
        entity.setDni(domain.getDni());
        entity.setActive(domain.isActive());
        entity.setRole(domain.getRole());
        entity.setGymId(domain.getGymId());
        return entity;
    }
}
