package com.trainingapp.trainingapp.infrastructure.repository.jpa.impl.exercise;

import com.trainingapp.trainingapp.domain.entity.exercise.MuscleGroup;
import com.trainingapp.trainingapp.domain.repository.exercise.MuscleGroupRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.exercise.MuscleGroupMapper;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.exercise.MuscleGroupJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MuscleGroupRepositoryImpl implements MuscleGroupRepository {

    private final MuscleGroupJpaRepository jpaRepository;
    private final MuscleGroupMapper mapper;

    public MuscleGroupRepositoryImpl(MuscleGroupJpaRepository jpaRepository, MuscleGroupMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<MuscleGroup> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<MuscleGroup> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
}
