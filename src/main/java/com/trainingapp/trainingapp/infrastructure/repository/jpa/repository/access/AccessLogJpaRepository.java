package com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.access;

import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.access.AccessLogJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccessLogJpaRepository extends JpaRepository<AccessLogJpaEntity, Long> {

    List<AccessLogJpaEntity> findByGymIdOrderByTimestampDesc(Long gymId);

    List<AccessLogJpaEntity> findByMemberIdOrderByTimestampDesc(Long memberId);
}