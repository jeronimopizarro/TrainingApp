package com.trainingapp.trainingapp.infrastructure.repository.jpa.impl.user;

import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.domain.repository.user.TrainerRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.user.TrainerJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.user.TrainerMapper;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.user.TrainerJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainerRepositoryImpl implements TrainerRepository {

    private final TrainerJpaRepository jpaRepository;
    private final TrainerMapper mapper;

    public TrainerRepositoryImpl(TrainerJpaRepository jpaRepository, TrainerMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Trainer save(Trainer trainer) {
        TrainerJpaEntity entity = mapper.toJpaEntity(trainer);
        TrainerJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Trainer> findByGymId(Long gymId) {
        return jpaRepository.findByGymId(gymId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
