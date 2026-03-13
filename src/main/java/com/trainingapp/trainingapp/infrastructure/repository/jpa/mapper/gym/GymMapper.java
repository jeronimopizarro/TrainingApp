package com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.gym;

import com.trainingapp.trainingapp.domain.entity.gym.Gym;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.gym.GymJpaEntity;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class GymMapper {

    public Gym toDomain(GymJpaEntity jpaEntity) {
        if (jpaEntity == null) return null;

        return new Gym(jpaEntity.getId(), jpaEntity.getName(), jpaEntity.getAddress(),
                jpaEntity.getPhone(),  jpaEntity.isActive());
    }

    public GymJpaEntity toJpaEntity(Gym gym) {
        if (gym == null) return null;
        GymJpaEntity jpaEntity = new GymJpaEntity();
        jpaEntity.setId(gym.getId());
        jpaEntity.setName(gym.getName());
        jpaEntity.setAddress(gym.getAddress());
        jpaEntity.setPhone(gym.getPhone());

        if (!gym.isActive()) {
            jpaEntity.setDeletedAt(LocalDateTime.now());
        }else{
            jpaEntity.setDeletedAt(null);
        }
        return jpaEntity;
    }
}