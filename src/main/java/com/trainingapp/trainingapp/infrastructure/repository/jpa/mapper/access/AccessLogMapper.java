package com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.access;

import com.trainingapp.trainingapp.domain.entity.Access.AccessLog;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.access.AccessLogJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AccessLogMapper {

    public AccessLog toDomain(AccessLogJpaEntity entity) {
        if (entity == null) return null;

        return AccessLog.restore(
                entity.getId(),
                entity.getMemberId(),
                entity.getGymId(),
                entity.getTimestamp(),
                entity.isAccessGranted(),
                entity.getMessage()
        );
    }

    public AccessLogJpaEntity toEntity(AccessLog domain) {
        if (domain == null) {
            return null;
        }
        return new AccessLogJpaEntity(
                domain.getId(),
                domain.getMemberId(),
                domain.getGymId(),
                domain.getTimestamp(),
                domain.isAccessGranted(),
                domain.getMessage()
        );
    }
}