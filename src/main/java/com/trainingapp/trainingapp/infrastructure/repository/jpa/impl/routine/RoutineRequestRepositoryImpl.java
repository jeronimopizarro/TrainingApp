package com.trainingapp.trainingapp.infrastructure.repository.jpa.impl.routine;

import com.trainingapp.trainingapp.domain.entity.routine.RoutineRequest;
import com.trainingapp.trainingapp.domain.enums.routine.RoutineRequestStatus;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRequestRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.routine.RoutineRequestMapper;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.routine.RoutineRequestJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RoutineRequestRepositoryImpl implements RoutineRequestRepository {

    private final RoutineRequestJpaRepository jpaRepository;
    private final RoutineRequestMapper mapper;

    public RoutineRequestRepositoryImpl(RoutineRequestJpaRepository jpaRepository, RoutineRequestMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public RoutineRequest save(RoutineRequest routineRequest) {
        var entity = mapper.toEntity(routineRequest);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<RoutineRequest> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByMemberIdAndStatus(Long memberId, RoutineRequestStatus status) {
        return jpaRepository.existsByMemberIdAndStatus(memberId, status);
    }

    @Override
    public List<RoutineRequest> findByGymIdAndStatus(Long gymId, RoutineRequestStatus status) {
        return jpaRepository.findByGymIdAndStatus(gymId, status).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<RoutineRequest> findFirstByMemberIdAndStatus(Long memberId, RoutineRequestStatus status) {
        return jpaRepository.findFirstByMemberIdAndStatus(memberId, status)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<RoutineRequest> findFirstByMemberIdAndStatusAndAssignedTrainerId(Long memberId, RoutineRequestStatus status, Long assignedTrainerId) {
        return jpaRepository.findFirstByMemberIdAndStatusAndAssignedTrainerId(memberId, status, assignedTrainerId)
                .map(mapper::toDomain);
    }

    @Override
    public List<RoutineRequest> findByAssignedTrainerIdAndStatus(Long assignedTrainerId, RoutineRequestStatus status) {
        return jpaRepository.findByAssignedTrainerIdAndStatus(assignedTrainerId, status).stream()
                .map(mapper::toDomain)
                .toList();
    }
}