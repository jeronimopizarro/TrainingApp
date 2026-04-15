package com.trainingapp.trainingapp.application.useCase.dashboard;

import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.entity.routine.TrainingDay;
import com.trainingapp.trainingapp.domain.entity.subscription.Subscription;
import com.trainingapp.trainingapp.domain.entity.tracker.TrainingSession;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.routine.RoutineStatus;
import com.trainingapp.trainingapp.domain.enums.routine.RoutineRequestStatus;
import com.trainingapp.trainingapp.domain.enums.subscription.SubscriptionStatus;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRequestRepository;
import com.trainingapp.trainingapp.domain.repository.subscription.SubscriptionRepository;
import com.trainingapp.trainingapp.domain.repository.tracker.TrainingSessionRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.dashboard.MemberDashboardResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberDashboardUseCaseTest {

    @Mock private SecurityUtils securityUtils;
    @Mock private SubscriptionRepository subscriptionRepository;
    @Mock private RoutineRepository routineRepository;
    @Mock private TrainingSessionRepository trainingSessionRepository;
    @Mock private RoutineRequestRepository routineRequestRepository;

    @InjectMocks private MemberDashboardUseCase useCase;

    @Test
    @DisplayName("Debería compilar métricas del alumno (Días de expiración, Rutina activa, Fechas entrenadas)")
    void shouldReturnMemberDashboardMetrics() {
        // Arrange
        User mockMember = mock(User.class);
        when(mockMember.getId()).thenReturn(100L);
        when(securityUtils.getCurrentUser()).thenReturn(mockMember);

        // 1. Mock de Suscripción (vence en 10 días)
        Subscription mockSub = Subscription.restore(1L, 100L, 1L, "Mensual", LocalDate.now().minusDays(20), LocalDate.now().plusDays(10), SubscriptionStatus.ACTIVE);
        when(subscriptionRepository.findActiveByMemberId(100L)).thenReturn(Optional.of(mockSub));

        // 2. Mock de Rutina Activa y su Día Sugerido
        Routine mockRoutine = mock(Routine.class);
        when(mockRoutine.getId()).thenReturn(1L);
        when(mockRoutine.getName()).thenReturn("Rutina Hipertrofia");
        when(routineRepository.findByMemberIdAndStatus(100L, RoutineStatus.ACTIVE)).thenReturn(Optional.of(mockRoutine));

        TrainingSession lastSession = mock(TrainingSession.class);
        when(lastSession.getTrainingDayId()).thenReturn(5L);
        when(trainingSessionRepository.findLastSessionByMemberIdAndRoutineId(100L, 1L)).thenReturn(Optional.of(lastSession));

        TrainingDay nextDay = mock(TrainingDay.class);
        when(nextDay.getId()).thenReturn(6L);
        when(nextDay.getName()).thenReturn("Día de Piernas");
        when(mockRoutine.getNextTrainingDay(5L)).thenReturn(nextDay); // Resuelve el algoritmo de tu entidad

        // 3. Mock de Fechas de Entrenamiento del Mes
        List<LocalDate> dates = List.of(LocalDate.now().minusDays(2), LocalDate.now().minusDays(1));
        when(trainingSessionRepository.findTrainingDatesByMemberIdAndMonth(eq(100L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(dates);

        // 4. Mock de Solicitud Pendiente
        when(routineRequestRepository.existsByMemberIdAndStatus(eq(100L), eq(RoutineRequestStatus.PENDING)))
                .thenReturn(false);

        // Act
        MemberDashboardResponse response = useCase.execute();

        // Assert
        assertEquals(10, response.daysUntilExpiration(), "Debe calcular exactamente 10 días restantes");

        assertNotNull(response.activeRoutine());
        assertEquals("Rutina Hipertrofia", response.activeRoutine().name());
        assertEquals("Día de Piernas", response.activeRoutine().suggestedDay().name(), "Debe sugerir el día siguiente según el algoritmo");

        assertEquals(2, response.trainingDaysThisMonth().size());
        assertEquals(false, response.hasPendingRequest());
    }
}