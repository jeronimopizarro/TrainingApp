package com.trainingapp.trainingapp.infrastructure.repository.jpa.impl.routine;

import com.trainingapp.trainingapp.domain.entity.routine.RoutineSummary;
import com.trainingapp.trainingapp.domain.enums.routine.RoutineStatus;
import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.routine.RoutineJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.routine.RoutineMapper;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.routine.RoutineJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class RoutineRepositoryImpl implements RoutineRepository {

    private final RoutineJpaRepository jpaRepository;
    private final RoutineMapper mapper;

    public RoutineRepositoryImpl(RoutineJpaRepository jpaRepository, RoutineMapper mapper) {
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
    public Optional<Routine> findByMemberIdAndStatus(Long memberId, RoutineStatus status) {
        return jpaRepository.findByMemberIdAndStatusAndActiveTrue(memberId, status).map(mapper::toDomain);
    }

    @Override
    public List<RoutineSummary> findAllSummariesByMemberId(Long memberId) {
        return jpaRepository.findAllSummariesByMemberId(memberId);
    }

    @Override
    public List<RoutineSummary> findAllSummariesByTrainerId(Long trainerId) {
        return jpaRepository.findAllSummariesByTrainerId(trainerId);
    }

    @Override
    public List<RoutineSummary> findAllBaseRoutinesByGymId(Long gymId) {
        return jpaRepository.findAllBaseRoutinesByGymId(gymId);
    }

    @Override
    public boolean existsActiveByMemberId(Long memberId) {
        return jpaRepository.existsByMemberIdAndStatusAndActiveTrue(memberId, RoutineStatus.ACTIVE);
    }
}