package com.trainingapp.trainingapp.application.useCase.tracker;

import com.trainingapp.trainingapp.application.mapper.tracker.TrainingSessionDTOMapper;
import com.trainingapp.trainingapp.domain.entity.tracker.SetLog;
import com.trainingapp.trainingapp.domain.entity.tracker.TrainingSession;
import com.trainingapp.trainingapp.domain.repository.tracker.TrainingSessionRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.tracker.LogSetRequest;
import com.trainingapp.trainingapp.web.dto.tracker.SetLogResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LogTrainingSetUseCase {

    private final TrainingSessionRepository trainingSessionRepository;
    private final SecurityUtils securityUtils;
    private final TrainingSessionDTOMapper trainingSessionDTOMapper;

    public LogTrainingSetUseCase(TrainingSessionRepository trainingSessionRepository,
                                 SecurityUtils securityUtils,
                                 TrainingSessionDTOMapper trainingSessionDTOMapper) {
        this.trainingSessionRepository = trainingSessionRepository;
        this.securityUtils = securityUtils;
        this.trainingSessionDTOMapper = trainingSessionDTOMapper;
    }

    @Transactional
    public SetLogResponse execute(Long sessionId, LogSetRequest request) {
        Long currentMemberId = securityUtils.getCurrentUser().getId();

        TrainingSession session = getSessionAndValidateOwnership(sessionId, currentMemberId);
        SetLog newSet = createSetLogFromRequest(request);

        TrainingSession savedSession = persistSetLog(session, newSet);
        SetLog savedSet = savedSession.getSets().get(savedSession.getSets().size() - 1);

        return trainingSessionDTOMapper.toSetLogResponse(savedSet);
    }


    private TrainingSession getSessionAndValidateOwnership(Long sessionId, Long memberId) {
        TrainingSession session = trainingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("La sesión de entrenamiento no existe."));

        if (!session.getMemberId().equals(memberId)) {
            throw new IllegalStateException("Acceso denegado: No puedes registrar series en una sesión que no te pertenece.");
        }

        return session;
    }

    private SetLog createSetLogFromRequest(LogSetRequest request) {
        return SetLog.recordNew(
                request.exerciseId(),
                request.setNumber(),
                request.repsPerformed(),
                request.weightLifted(),
                request.rir(),
                request.notes()
        );
    }

    private TrainingSession persistSetLog(TrainingSession session, SetLog newSet) {
        session.addSet(newSet);
        return trainingSessionRepository.save(session);
    }
}