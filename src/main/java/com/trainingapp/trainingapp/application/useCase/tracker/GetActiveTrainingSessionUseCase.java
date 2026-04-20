package com.trainingapp.trainingapp.application.useCase.tracker;

import com.trainingapp.trainingapp.application.mapper.tracker.TrainingSessionDTOMapper;
import com.trainingapp.trainingapp.domain.repository.tracker.TrainingSessionRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.tracker.SessionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetActiveTrainingSessionUseCase {

    private final TrainingSessionRepository sessionRepository;
    private final SecurityUtils securityUtils;
    private final TrainingSessionDTOMapper mapper;

    public GetActiveTrainingSessionUseCase(TrainingSessionRepository sessionRepository,
                                           SecurityUtils securityUtils,
                                           TrainingSessionDTOMapper mapper) {
        this.sessionRepository = sessionRepository;
        this.securityUtils = securityUtils;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public SessionResponse execute() {
        Long memberId = securityUtils.getCurrentUser().getId();
        return sessionRepository.findActiveSessionByMemberId(memberId)
                .map(mapper::toResponse)
                .orElse(null);
    }
}
