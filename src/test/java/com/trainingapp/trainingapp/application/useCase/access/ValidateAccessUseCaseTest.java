package com.trainingapp.trainingapp.application.useCase.access;

import com.trainingapp.trainingapp.domain.entity.Access.AccessLog;
import com.trainingapp.trainingapp.domain.entity.subscription.Subscription;
import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.enums.access.AccessMethod;
import com.trainingapp.trainingapp.domain.exception.access.InvalidAccessLogException;
import com.trainingapp.trainingapp.domain.repository.Access.AccessLogRepository;
import com.trainingapp.trainingapp.domain.repository.subscription.SubscriptionRepository;
import com.trainingapp.trainingapp.domain.repository.user.MemberRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.JwtService;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.access.ValidateAccessRequest;
import com.trainingapp.trainingapp.web.dto.access.ValidateAccessResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidateAccessUseCaseTest {

    @Mock private JwtService jwtService;
    @Mock private MemberRepository memberRepository;
    @Mock private SecurityUtils securityUtils;
    @Mock private SubscriptionRepository subscriptionRepository;
    @Mock private AccessLogRepository accessLogRepository;

    @InjectMocks private ValidateAccessUseCase useCase;

    @Test
    @DisplayName("Debería permitir el acceso si el miembro tiene una suscripción activa")
    void shouldGrantAccessWhenSubscriptionIsActive() {
        Long gymId = 10L;
        Long memberId = 100L;
        // CORRECCIÓN: Orden de parámetros correcto (identificador, method)
        ValidateAccessRequest request = new ValidateAccessRequest("12345678", AccessMethod.DNI);
        Member mockMember = mock(Member.class);
        Subscription mockSubscription = mock(Subscription.class);

        when(securityUtils.getCurrentUserGymId()).thenReturn(gymId);

        // CORRECCIÓN: Uso de findByDni
        when(memberRepository.findByDni("12345678")).thenReturn(Optional.of(mockMember));
        when(mockMember.getId()).thenReturn(memberId);
        when(mockMember.getFirstName()).thenReturn("Juan");
        when(mockMember.getLastName()).thenReturn("Perez");
        when(mockMember.isActive()).thenReturn(true);
        when(mockMember.getGymId()).thenReturn(gymId);

        when(subscriptionRepository.findActiveByMemberId(memberId)).thenReturn(Optional.of(mockSubscription));

        // Simular guardado del log
        when(accessLogRepository.save(any(AccessLog.class))).thenReturn(mock(AccessLog.class));

        ValidateAccessResponse response = useCase.execute(request);

        assertTrue(response.accessGranted());
        verify(accessLogRepository).save(any(AccessLog.class));
    }

    @Test
    @DisplayName("Debería denegar el acceso si el miembro está inactivo")
    void shouldDenyAccessWhenMemberIsInactive() {
        Long gymId = 10L;
        // CORRECCIÓN: Orden de parámetros correcto
        ValidateAccessRequest request = new ValidateAccessRequest("12345678", AccessMethod.DNI);
        Member mockMember = mock(Member.class);

        when(securityUtils.getCurrentUserGymId()).thenReturn(gymId);

        // CORRECCIÓN: Uso de findByDni
        when(memberRepository.findByDni("12345678")).thenReturn(Optional.of(mockMember));
        when(mockMember.getId()).thenReturn(100L);
        when(mockMember.getFirstName()).thenReturn("Juan");
        when(mockMember.getLastName()).thenReturn("Perez");
        when(mockMember.isActive()).thenReturn(false); // Inactivo

        when(accessLogRepository.save(any(AccessLog.class))).thenReturn(mock(AccessLog.class));

        ValidateAccessResponse response = useCase.execute(request);

        assertFalse(response.accessGranted());
        verify(subscriptionRepository, never()).findActiveByMemberId(any());
    }

    @Test
    @DisplayName("Debería denegar el acceso si no hay suscripción activa")
    void shouldDenyAccessWhenNoActiveSubscription() {
        Long gymId = 10L;
        Long memberId = 100L;
        ValidateAccessRequest request = new ValidateAccessRequest("12345678", AccessMethod.DNI);
        Member mockMember = mock(Member.class);

        when(securityUtils.getCurrentUserGymId()).thenReturn(gymId);
        when(memberRepository.findByDni("12345678")).thenReturn(Optional.of(mockMember));
        when(mockMember.getId()).thenReturn(memberId);
        when(mockMember.getFirstName()).thenReturn("Juan");
        when(mockMember.getLastName()).thenReturn("Perez");
        when(mockMember.isActive()).thenReturn(true);
        when(mockMember.getGymId()).thenReturn(gymId);
        when(subscriptionRepository.findActiveByMemberId(memberId)).thenReturn(Optional.empty()); // Sin sub activa
        when(accessLogRepository.save(any(AccessLog.class))).thenReturn(mock(AccessLog.class));

        ValidateAccessResponse response = useCase.execute(request);

        assertFalse(response.accessGranted());
    }

    @Test
    @DisplayName("Debería lanzar error de Log Inválido si el DNI no pertenece a ningún miembro")
    void shouldThrowExceptionWhenMemberNotFoundByDni() {
        Long gymId = 10L;
        ValidateAccessRequest request = new ValidateAccessRequest("99999999", AccessMethod.DNI);

        when(securityUtils.getCurrentUserGymId()).thenReturn(gymId);
        when(memberRepository.findByDni("99999999")).thenReturn(Optional.empty());

        assertThrows(InvalidAccessLogException.class, () -> useCase.execute(request));
        verify(accessLogRepository, never()).save(any());
    }
}