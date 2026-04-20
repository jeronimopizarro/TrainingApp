package com.trainingapp.trainingapp.application.useCase.tracker;

import com.trainingapp.trainingapp.application.mapper.tracker.TrainingSessionDTOMapper;
import com.trainingapp.trainingapp.domain.entity.tracker.TrainingSession;
import com.trainingapp.trainingapp.domain.exception.tracker.TrainingSessionNotFoundException;
import com.trainingapp.trainingapp.domain.repository.tracker.TrainingSessionRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.tracker.SessionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CancelTrainingSessionUseCase {

    private final TrainingSessionRepository sessionRepository;
    private final SecurityUtils securityUtils;
    private final TrainingSessionDTOMapper mapper;

    public CancelTrainingSessionUseCase(TrainingSessionRepository sessionRepository,
                                        SecurityUtils securityUtils,
                                        TrainingSessionDTOMapper mapper) {
        this.sessionRepository = sessionRepository;
        this.securityUtils = securityUtils;
        this.mapper = mapper;
    }

    @Transactional
    public SessionResponse execute(Long sessionId) {
        TrainingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new TrainingSessionNotFoundException(sessionId));

        securityUtils.validateSameGym(session.getGymId());

        session.cancel();
        TrainingSession cancelledSession = sessionRepository.save(session);
        return mapper.toResponse(cancelledSession);
    }
}
