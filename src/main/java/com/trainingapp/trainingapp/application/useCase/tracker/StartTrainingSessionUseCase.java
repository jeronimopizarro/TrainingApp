package com.trainingapp.trainingapp.application.useCase.tracker;

import com.trainingapp.trainingapp.application.mapper.tracker.TrainingSessionDTOMapper;
import com.trainingapp.trainingapp.application.useCase.subscription.GetActiveSubscriptionByMemberUseCase;
import com.trainingapp.trainingapp.domain.entity.tracker.TrainingSession;
import com.trainingapp.trainingapp.domain.exception.subscription.ActiveSubscriptionNotFoundException;
import com.trainingapp.trainingapp.domain.repository.tracker.TrainingSessionRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.tracker.SessionResponse;
import com.trainingapp.trainingapp.web.dto.tracker.StartSessionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StartTrainingSessionUseCase {

    private final TrainingSessionRepository trainingSessionRepository;
    private final GetActiveSubscriptionByMemberUseCase getActiveSubscriptionUseCase;
    private final SecurityUtils securityUtils;
    private final TrainingSessionDTOMapper trainingSessionDTOMapper;

    public StartTrainingSessionUseCase(TrainingSessionRepository trainingSessionRepository,
                                       GetActiveSubscriptionByMemberUseCase getActiveSubscriptionUseCase,
                                       SecurityUtils securityUtils,
                                       TrainingSessionDTOMapper trainingSessionDTOMapper) {
        this.trainingSessionRepository = trainingSessionRepository;
        this.getActiveSubscriptionUseCase = getActiveSubscriptionUseCase;
        this.securityUtils = securityUtils;
        this.trainingSessionDTOMapper = trainingSessionDTOMapper;
    }

    @Transactional
    public SessionResponse execute(StartSessionRequest request) {
        Long currentMemberId = securityUtils.getCurrentUser().getId();
        Long currentGymId = securityUtils.getCurrentUserGymId();

        validateMemberCanTrain(currentMemberId);
        ensureNoActiveSessionExists(currentMemberId);

        TrainingSession newSession =
                createAndPersistSession(currentMemberId, request.routineId(), currentGymId);
        return trainingSessionDTOMapper.toResponse(newSession);
    }

    private void validateMemberCanTrain(Long memberId) {
        try {
            getActiveSubscriptionUseCase.execute(memberId);
        } catch (ActiveSubscriptionNotFoundException ex) {
            throw new IllegalStateException(
                    "Acceso denegado: No puedes iniciar un entrenamiento sin una membresía activa.");
        }
    }

    private void ensureNoActiveSessionExists(Long memberId) {
        trainingSessionRepository.findActiveSessionByMemberId(memberId)
                .ifPresent(session -> {
                    throw new IllegalStateException(
                            "Ya tienes un entrenamiento en progreso (ID: " + session.getId() + "). Finalízalo antes de iniciar uno nuevo.");
                });
    }

    private TrainingSession createAndPersistSession(Long memberId, Long routineId, Long gymId) {
        TrainingSession session = TrainingSession.startNew(memberId, routineId, gymId);
        return trainingSessionRepository.save(session);
    }
}