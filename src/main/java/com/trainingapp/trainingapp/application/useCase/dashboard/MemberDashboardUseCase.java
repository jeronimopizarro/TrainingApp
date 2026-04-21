package com.trainingapp.trainingapp.application.useCase.dashboard;

import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.entity.routine.TrainingDay;
import com.trainingapp.trainingapp.domain.entity.subscription.Subscription;
import com.trainingapp.trainingapp.domain.entity.tracker.TrainingSession;
import com.trainingapp.trainingapp.domain.enums.routine.RoutineStatus;
import com.trainingapp.trainingapp.domain.enums.routine.RoutineRequestStatus;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRequestRepository;
import com.trainingapp.trainingapp.domain.repository.subscription.SubscriptionRepository;
import com.trainingapp.trainingapp.domain.repository.tracker.TrainingSessionRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.dashboard.MemberDashboardResponse;
import com.trainingapp.trainingapp.web.dto.dashboard.MemberDashboardResponse.ActiveRoutineDTO;
import com.trainingapp.trainingapp.web.dto.dashboard.MemberDashboardResponse.SuggestedDayDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class MemberDashboardUseCase {

    private final SecurityUtils securityUtils;
    private final SubscriptionRepository subscriptionRepository;
    private final RoutineRepository routineRepository;
    private final TrainingSessionRepository trainingSessionRepository;
    private final RoutineRequestRepository routineRequestRepository;

    public MemberDashboardUseCase(SecurityUtils securityUtils,
                                  SubscriptionRepository subscriptionRepository,
                                  RoutineRepository routineRepository,
                                  TrainingSessionRepository trainingSessionRepository,
                                  RoutineRequestRepository routineRequestRepository) {
        this.securityUtils = securityUtils;
        this.subscriptionRepository = subscriptionRepository;
        this.routineRepository = routineRepository;
        this.trainingSessionRepository = trainingSessionRepository;
        this.routineRequestRepository = routineRequestRepository;
    }

    @Transactional(readOnly = true)
    public MemberDashboardResponse execute() {
        Long memberId = securityUtils.getCurrentUser().getId();
        LocalDate today = LocalDate.now();

        Integer daysUntilExpiration = getDaysUntilExpiration(memberId, today);
        ActiveRoutineDTO activeRoutine = getActiveRoutine(memberId);
        List<LocalDate> trainingDays = getTrainingDaysThisMonth(memberId, today);
        boolean hasPendingRequest = routineRequestRepository.existsByMemberIdAndStatus(memberId, RoutineRequestStatus.PENDING);

        return new MemberDashboardResponse(daysUntilExpiration, activeRoutine, trainingDays, hasPendingRequest);
    }

    private Integer getDaysUntilExpiration(Long memberId, LocalDate today) {
        Optional<Subscription> subscription = subscriptionRepository.findActiveByMemberId(memberId);

        if (subscription.isEmpty() || subscription.get().getEndDate() == null) {
            return null; // No tiene cuota activa
        }

        long daysBetween = ChronoUnit.DAYS.between(today, subscription.get().getEndDate());
        // Si ya se venció, pero por algún motivo sigue en estado ACTIVE, devolvemos 0
        return daysBetween < 0 ? 0 : (int) daysBetween;
    }

    private ActiveRoutineDTO getActiveRoutine(Long memberId) {
        Optional<Routine> routineOpt =
                routineRepository.findByMemberIdAndStatus(memberId, RoutineStatus.ACTIVE);

        if (routineOpt.isEmpty()) return null;

        Routine routine = routineOpt.get();
        // LLAMAMOS AL NUEVO ALGORITMO
        MemberDashboardResponse.SuggestedDayDTO
                suggestedDay = calculateNextTrainingDay(memberId, routine);

        return new ActiveRoutineDTO(
                routine.getId(),
                routine.getName(),
                routine.getEndDate(),
                suggestedDay
        );
    }

    private SuggestedDayDTO calculateNextTrainingDay(Long memberId, Routine routine) {
        // 1. Obtenemos el ID del último día que entrenó (o null si nunca entrenó)
        Long lastCompletedDayId = trainingSessionRepository
                .findLastSessionByMemberIdAndRoutineId(memberId, routine.getId())
                .map(TrainingSession::getTrainingDayId)
                .orElse(null);

        TrainingDay nextDay = routine.getNextTrainingDay(lastCompletedDayId);

        return nextDay != null ? new SuggestedDayDTO(nextDay.getId(), nextDay.getName()) : null;
    }

    private List<LocalDate> getTrainingDaysThisMonth(Long memberId, LocalDate today) {
        Instant startOfMonth = today.withDayOfMonth(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endOfMonth = today.withDayOfMonth(today.lengthOfMonth()).atTime(23, 59, 59).toInstant(ZoneOffset.UTC);

        return trainingSessionRepository.findTrainingDatesByMemberIdAndMonth(memberId, startOfMonth, endOfMonth);
    }
}