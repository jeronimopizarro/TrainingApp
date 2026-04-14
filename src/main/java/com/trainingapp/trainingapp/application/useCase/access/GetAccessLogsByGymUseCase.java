package com.trainingapp.trainingapp.application.useCase.access;

import com.trainingapp.trainingapp.application.mapper.access.AccessLogDTOMapper;
import com.trainingapp.trainingapp.domain.entity.Access.AccessLog;
import com.trainingapp.trainingapp.domain.repository.Access.AccessLogRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.access.GymAccessSummaryResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class GetAccessLogsByGymUseCase {

    private final AccessLogRepository accessLogRepository;
    private final SecurityUtils securityUtils;
    private final AccessLogDTOMapper accessLogDTOMapper;

    public GetAccessLogsByGymUseCase(AccessLogRepository accessLogRepository, SecurityUtils securityUtils,
                                     AccessLogDTOMapper accessLogDTOMapper) {
        this.accessLogRepository = accessLogRepository;
        this.securityUtils = securityUtils;
        this.accessLogDTOMapper = accessLogDTOMapper;
    }

    @Transactional(readOnly = true)
    public GymAccessSummaryResponse execute(Boolean granted) {
        Long gymId = securityUtils.getCurrentUserGymId();

        List<AccessLog> allLogs = accessLogRepository.findByGymId(gymId);
        List<AccessLog> filteredLogs = granted == null 
            ? allLogs 
            : accessLogRepository.findByGymIdAndStatus(gymId, granted);

        LocalDate today = LocalDate.now();

        long successfulToday = countSuccessfulEntriesToday(allLogs, today);
        long failedToday = countFailedAttemptsToday(allLogs, today);

        return accessLogDTOMapper.toGymSummaryResponse(successfulToday, failedToday, filteredLogs);
    }

    private long countSuccessfulEntriesToday(List<AccessLog> logs, LocalDate today) {
        return logs.stream()
                .filter(log -> log.getTimestamp().toLocalDate().equals(today))
                .filter(AccessLog::isAccessGranted)
                .count();
    }

    private long countFailedAttemptsToday(List<AccessLog> logs, LocalDate today) {
        return logs.stream()
                .filter(log -> log.getTimestamp().toLocalDate().equals(today))
                .filter(log -> !log.isAccessGranted())
                .count();
    }
}
