package com.trainingapp.trainingapp.domain.repository.Access;

import com.trainingapp.trainingapp.domain.entity.Access.AccessLog;

import java.util.List;

public interface AccessLogRepository {
    AccessLog save(AccessLog accessLog);

    List<AccessLog> findByGymId(Long gymId);

    List<AccessLog> findByMemberId(Long memberId);
}