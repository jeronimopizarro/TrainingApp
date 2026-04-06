package com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.gym;

import com.trainingapp.trainingapp.domain.entity.gym.Gym;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.gym.GymJpaEntity;
import org.springframework.stereotype.Component;


@Component
public class GymMapper {

    public Gym toDomain(GymJpaEntity entity) {
        if (entity == null) return null;

        return Gym.restore(
                entity.getId(),
                entity.getName(),
                entity.getAddress(),
                entity.getPhoneNumber(),
                entity.isActive());
    }

    public GymJpaEntity toJpaEntity(Gym gym) {
        if (gym == null) return null;

        GymJpaEntity entity = new GymJpaEntity();
        entity.setId(gym.getId());
        entity.setName(gym.getName());
        entity.setAddress(gym.getAddress());
        entity.setPhoneNumber(gym.getPhoneNumber());
        entity.setActive(gym.isActive());

        return entity;
    }
}