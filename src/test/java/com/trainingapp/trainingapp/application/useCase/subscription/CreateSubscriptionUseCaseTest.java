package com.trainingapp.trainingapp.application.useCase.subscription;

import com.trainingapp.trainingapp.application.mapper.subscription.SubscriptionDTOMapper;
import com.trainingapp.trainingapp.application.useCase.transaction.RegisterTransactionCommand;
import com.trainingapp.trainingapp.application.useCase.transaction.RegisterTransactionUseCase;
import com.trainingapp.trainingapp.application.validator.GymValidator;
import com.trainingapp.trainingapp.application.validator.MemberAccessValidator;
import com.trainingapp.trainingapp.domain.entity.membership.MembershipPlan;
import com.trainingapp.trainingapp.domain.entity.subscription.Subscription;
import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.subscription.SubscriptionStatus;
import com.trainingapp.trainingapp.domain.enums.transaction.PaymentMethod;
import com.trainingapp.trainingapp.domain.exception.membership.InactiveMembershipPlanException;
import com.trainingapp.trainingapp.domain.exception.subscription.ActiveSubscriptionAlreadyExistsException;
import com.trainingapp.trainingapp.domain.exception.subscription.InvalidSubscriptionStartDateException;
import com.trainingapp.trainingapp.domain.repository.membership.MembershipPlanRepository;
import com.trainingapp.trainingapp.domain.repository.subscription.SubscriptionRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.subscription.CreateSubscriptionRequest;
import com.trainingapp.trainingapp.web.dto.subscription.SubscriptionResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateSubscriptionUseCaseTest {

    @Mock private SubscriptionRepository subscriptionRepository;
    @Mock private MembershipPlanRepository planRepository;
    @Mock private SubscriptionDTOMapper subscriptionDTOMapper;
    @Mock private SecurityUtils securityUtils;
    @Mock private GymValidator gymValidator;
    @Mock private MemberAccessValidator memberAccessValidator;
    @Mock private RegisterTransactionUseCase registerTransactionUseCase;

    @InjectMocks private CreateSubscriptionUseCase useCase;

    @Test
    @DisplayName("Debería crear una suscripción exitosamente y registrar la transacción")
    void shouldCreateSubscriptionSuccessfully() {
        CreateSubscriptionRequest request = new CreateSubscriptionRequest(
                100L, 1L, LocalDate.now(), PaymentMethod.CARD
        );

        when(securityUtils.getCurrentUserGymId()).thenReturn(10L);
        doNothing().when(gymValidator).validateExists(10L);

        Member mockMember = mock(Member.class);
        when(memberAccessValidator.findMemberAndValidateAccess(100L)).thenReturn(mockMember);

        MembershipPlan mockPlan = MembershipPlan.restore(
                1L, "Mensual", "Pase libre", new BigDecimal("5000.00"), 1, 10L, true
        );
        when(planRepository.findById(1L)).thenReturn(Optional.of(mockPlan));
        doNothing().when(securityUtils).validateSameGym(10L);

        when(subscriptionRepository.findActiveByMemberId(100L)).thenReturn(Optional.empty());

        Subscription mockSubscription = mock(Subscription.class);
        when(mockSubscription.getId()).thenReturn(50L);

        when(subscriptionDTOMapper.toDomain(request, "Mensual", 1)).thenReturn(mockSubscription);
        when(subscriptionRepository.save(mockSubscription)).thenReturn(mockSubscription);

        // Simulamos usuario logueado para el registro de la transacción
        User mockAdmin = mock(User.class);
        when(mockAdmin.getId()).thenReturn(2L);
        when(securityUtils.getCurrentUser()).thenReturn(mockAdmin);

        SubscriptionResponse mockResponse = mock(SubscriptionResponse.class);
        when(subscriptionDTOMapper.toResponse(mockSubscription)).thenReturn(mockResponse);

        SubscriptionResponse response = useCase.execute(request);

        assertNotNull(response);
        verify(subscriptionRepository).save(mockSubscription);
        verify(registerTransactionUseCase).execute(any(RegisterTransactionCommand.class));
    }

    @Test
    @DisplayName("Debería lanzar error si la fecha de inicio es en el pasado")
    void shouldThrowException_WhenStartDateIsPast() {
        CreateSubscriptionRequest request = new CreateSubscriptionRequest(
                100L, 1L, LocalDate.now().minusDays(1), PaymentMethod.CASH // Fecha del pasado
        );

        when(securityUtils.getCurrentUserGymId()).thenReturn(10L);
        Member mockMember = mock(Member.class);
        when(memberAccessValidator.findMemberAndValidateAccess(100L)).thenReturn(mockMember);

        MembershipPlan mockPlan = MembershipPlan.restore(
                1L, "Mensual", "Pase", new BigDecimal("5000"), 1, 10L, true
        );
        when(planRepository.findById(1L)).thenReturn(Optional.of(mockPlan));

        assertThrows(InvalidSubscriptionStartDateException.class, () -> useCase.execute(request));

        // Verificamos que se cortó la ejecución antes de interactuar con la BD
        verify(subscriptionRepository, never()).save(any());
        verify(registerTransactionUseCase, never()).execute(any());
    }

    @Test
    @DisplayName("Debería lanzar error si el alumno YA TIENE una suscripción activa")
    void shouldThrowException_WhenMemberAlreadyHasActiveSubscription() {
        CreateSubscriptionRequest request = new CreateSubscriptionRequest(
                100L, 1L, LocalDate.now(), PaymentMethod.CASH
        );

        when(securityUtils.getCurrentUserGymId()).thenReturn(10L);
        Member mockMember = mock(Member.class);
        when(memberAccessValidator.findMemberAndValidateAccess(100L)).thenReturn(mockMember);

        MembershipPlan mockPlan = MembershipPlan.restore(
                1L, "Mensual", "Pase", new BigDecimal("5000"), 1, 10L, true
        );
        when(planRepository.findById(1L)).thenReturn(Optional.of(mockPlan));

        // Simulamos que el repositorio encuentra una suscripción activa previa
        Subscription activeSub = mock(Subscription.class);
        when(activeSub.getEndDate()).thenReturn(LocalDate.now().plusDays(10));
        when(subscriptionRepository.findActiveByMemberId(100L)).thenReturn(Optional.of(activeSub));

        assertThrows(ActiveSubscriptionAlreadyExistsException.class, () -> useCase.execute(request));
    }

    @Test
    @DisplayName("Debería lanzar error si se intenta vender un plan inactivo")
    void shouldThrowException_WhenPlanIsInactive() {
        CreateSubscriptionRequest request = new CreateSubscriptionRequest(
                100L, 1L, LocalDate.now(), PaymentMethod.CASH
        );

        when(securityUtils.getCurrentUserGymId()).thenReturn(10L);
        Member mockMember = mock(Member.class);
        when(memberAccessValidator.findMemberAndValidateAccess(100L)).thenReturn(mockMember);

        // Simulamos un plan que ya no se vende (isActive = false)
        MembershipPlan inactivePlan = MembershipPlan.restore(
                1L, "Promo Verano", "Viejo", new BigDecimal("3000"), 1, 10L, false
        );
        when(planRepository.findById(1L)).thenReturn(Optional.of(inactivePlan));

        assertThrows(InactiveMembershipPlanException.class, () -> useCase.execute(request));
    }
}