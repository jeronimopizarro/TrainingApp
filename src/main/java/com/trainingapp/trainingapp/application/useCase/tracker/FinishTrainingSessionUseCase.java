package com.trainingapp.trainingapp.application.useCase.tracker;

import com.trainingapp.trainingapp.application.mapper.tracker.TrainingSessionDTOMapper;
import com.trainingapp.trainingapp.domain.entity.tracker.TrainingSession;
import com.trainingapp.trainingapp.domain.exception.tracker.TrainingSessionNotFoundException;
import com.trainingapp.trainingapp.domain.exception.tracker.UnauthorizedSessionAccessException;
import com.trainingapp.trainingapp.domain.repository.tracker.TrainingSessionRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.tracker.SessionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FinishTrainingSessionUseCase {

    private final TrainingSessionRepository trainingSessionRepository;
    private final SecurityUtils securityUtils;
    private final TrainingSessionDTOMapper trainingSessionDTOMapper;

    public FinishTrainingSessionUseCase(TrainingSessionRepository trainingSessionRepository,
                                        SecurityUtils securityUtils,
                                        TrainingSessionDTOMapper trainingSessionDTOMapper) {
        this.trainingSessionRepository = trainingSessionRepository;
        this.securityUtils = securityUtils;
        this.trainingSessionDTOMapper = trainingSessionDTOMapper;
    }

    @Transactional
    public SessionResponse execute(Long sessionId) {
        Long currentMemberId = securityUtils.getCurrentUser().getId();

        TrainingSession session = getSessionAndValidateOwnership(sessionId, currentMemberId);
        finishAndPersistSession(session);

        return trainingSessionDTOMapper.toResponse(session);
    }

    private TrainingSession getSessionAndValidateOwnership(Long sessionId, Long memberId) {
        TrainingSession session = trainingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new TrainingSessionNotFoundException(sessionId));

        if (!session.getMemberId().equals(memberId)) {
            throw new UnauthorizedSessionAccessException();
        }

        return session;
    }

    private void finishAndPersistSession(TrainingSession session) {
        session.finishSession();
        trainingSessionRepository.save(session);
    }
}