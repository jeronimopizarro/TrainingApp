package com.trainingapp.trainingapp.application.useCase.dashboard;

import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.entity.routine.TrainingDay;
import com.trainingapp.trainingapp.domain.entity.subscription.Subscription;
import com.trainingapp.trainingapp.domain.entity.tracker.TrainingSession;
import com.trainingapp.trainingapp.domain.enums.routine.RoutineStatus;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.domain.repository.subscription.SubscriptionRepository;
import com.trainingapp.trainingapp.domain.repository.tracker.TrainingSessionRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.dashboard.MemberDashboardResponse;
import com.trainingapp.trainingapp.web.dto.dashboard.MemberDashboardResponse.ActiveRoutineDTO;
import com.trainingapp.trainingapp.web.dto.dashboard.MemberDashboardResponse.SuggestedDayDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class MemberDashboardUseCase {

    private final SecurityUtils securityUtils;
    private final SubscriptionRepository subscriptionRepository;
    private final RoutineRepository routineRepository;
    private final TrainingSessionRepository trainingSessionRepository;

    public MemberDashboardUseCase(SecurityUtils securityUtils,
                                  SubscriptionRepository subscriptionRepository,
                                  RoutineRepository routineRepository,
                                  TrainingSessionRepository trainingSessionRepository) {
        this.securityUtils = securityUtils;
        this.subscriptionRepository = subscriptionRepository;
        this.routineRepository = routineRepository;
        this.trainingSessionRepository = trainingSessionRepository;
    }

    @Transactional(readOnly = true)
    public MemberDashboardResponse execute() {
        Long memberId = securityUtils.getCurrentUser().getId();
        LocalDate today = LocalDate.now();

        Integer daysUntilExpiration = getDaysUntilExpiration(memberId, today);
        ActiveRoutineDTO activeRoutine = getActiveRoutine(memberId);
        List<LocalDate> trainingDays = getTrainingDaysThisMonth(memberId, today);

        return new MemberDashboardResponse(daysUntilExpiration, activeRoutine, trainingDays);
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
        LocalDateTime startOfMonth = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = today.withDayOfMonth(today.lengthOfMonth()).atTime(LocalTime.MAX);

        return trainingSessionRepository.findTrainingDatesByMemberIdAndMonth(memberId, startOfMonth, endOfMonth);
    }
}