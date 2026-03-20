package com.trainingapp.trainingapp.application.useCase.access;

import com.trainingapp.trainingapp.application.mapper.access.AccessLogDTOMapper;
import com.trainingapp.trainingapp.domain.entity.Access.AccessLog;
import com.trainingapp.trainingapp.domain.repository.Access.AccessLogRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.access.MemberAccessSummaryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GetAccessLogsByMemberUseCase {

    private final AccessLogRepository accessLogRepository;
    private final SecurityUtils securityUtils;
    private final AccessLogDTOMapper accessLogDTOMapper;

    public GetAccessLogsByMemberUseCase(AccessLogRepository accessLogRepository, SecurityUtils securityUtils,
                                        AccessLogDTOMapper accessLogDTOMapper) {
        this.accessLogRepository = accessLogRepository;
        this.securityUtils = securityUtils;
        this.accessLogDTOMapper = accessLogDTOMapper;
    }

    @Transactional(readOnly = true)
    public MemberAccessSummaryResponse execute() {
        Long currentUserId = securityUtils.getCurrentUser().getId();

        List<AccessLog> logs = accessLogRepository.findByMemberId(currentUserId);

        long totalVisits = countTotalSuccessfulVisits(logs);

        return accessLogDTOMapper.toMemberSummaryResponse(totalVisits, logs);
    }

    private long countTotalSuccessfulVisits(List<AccessLog> logs) {
        return logs.stream()
                .filter(AccessLog::isAccessGranted)
                .count();
    }
}