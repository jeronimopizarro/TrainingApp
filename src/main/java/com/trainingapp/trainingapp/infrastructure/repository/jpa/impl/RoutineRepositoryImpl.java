package com.trainingapp.trainingapp.infrastructure.repository.jpa.impl;

import com.trainingapp.trainingapp.domain.entity.Routine;
import com.trainingapp.trainingapp.domain.repository.RoutineRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.RoutineJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.RoutineMapper;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.RoutineJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class RoutineRepositoryImpl implements RoutineRepository {

    private final RoutineJpaRepository jpaRepository;
    private final RoutineMapper mapper;

    public  RoutineRepositoryImpl(RoutineJpaRepository jpaRepository, RoutineMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Routine save(Routine routineDomain) {
        RoutineJpaEntity entityToSave = mapper.toEntity(routineDomain);

        RoutineJpaEntity savedEntity = jpaRepository.save(entityToSave);

        return mapper.toDomain(savedEntity);
    }
}
