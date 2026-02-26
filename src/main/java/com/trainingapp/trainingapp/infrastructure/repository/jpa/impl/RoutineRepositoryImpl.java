package com.trainingapp.trainingapp.infrastructure.repository.jpa.impl;

import com.trainingapp.trainingapp.domain.entity.Routine;
import com.trainingapp.trainingapp.domain.repository.RoutineRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.RoutineJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.RoutineMapper;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.RoutineJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

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

    @Override
    public Optional<Routine> findById(Long id) {
        Optional<RoutineJpaEntity> optionalJpaEntity = jpaRepository.findById(id);

        return optionalJpaEntity.map(mapper::toDomain);
    }

    @Override
    public List<Routine> findAllByMemberId(Long memberId) {
        List<RoutineJpaEntity> routineJpaEntities = jpaRepository.findAllByMemberId(memberId);

        return routineJpaEntities.stream().map(mapper::toDomain).toList();
    }
}