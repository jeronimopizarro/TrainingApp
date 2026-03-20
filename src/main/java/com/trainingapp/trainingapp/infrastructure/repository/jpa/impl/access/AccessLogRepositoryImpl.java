package com.trainingapp.trainingapp.infrastructure.repository.jpa.impl.access;

import com.trainingapp.trainingapp.domain.entity.Access.AccessLog;
import com.trainingapp.trainingapp.domain.repository.Access.AccessLogRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.access.AccessLogJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.access.AccessLogMapper;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.access.AccessLogJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class AccessLogRepositoryImpl implements AccessLogRepository {

    private final AccessLogJpaRepository jpaRepository;
    private final AccessLogMapper mapper;

    public AccessLogRepositoryImpl(AccessLogJpaRepository jpaRepository, AccessLogMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public AccessLog save(AccessLog accessLog) {
        AccessLogJpaEntity entity = mapper.toEntity(accessLog);
        AccessLogJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public List<AccessLog> findByGymId(Long gymId) {
        return jpaRepository.findByGymIdOrderByTimestampDesc(gymId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AccessLog> findByMemberId(Long memberId) {
        return jpaRepository.findByMemberIdOrderByTimestampDesc(memberId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}