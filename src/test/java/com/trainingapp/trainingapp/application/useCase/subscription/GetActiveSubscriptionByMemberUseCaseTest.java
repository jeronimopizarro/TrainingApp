package com.trainingapp.trainingapp.application.useCase.subscription;

import com.trainingapp.trainingapp.application.mapper.subscription.SubscriptionDTOMapper;
import com.trainingapp.trainingapp.application.validator.MemberAccessValidator;
import com.trainingapp.trainingapp.domain.entity.subscription.Subscription;
import com.trainingapp.trainingapp.domain.exception.subscription.ActiveSubscriptionNotFoundException;
import com.trainingapp.trainingapp.domain.repository.subscription.SubscriptionRepository;
import com.trainingapp.trainingapp.web.dto.subscription.SubscriptionResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetActiveSubscriptionByMemberUseCaseTest {

    @Mock private SubscriptionRepository subscriptionRepository;
    @Mock private SubscriptionDTOMapper subscriptionDTOMapper;
    @Mock private MemberAccessValidator memberAccessValidator;

    @InjectMocks private GetActiveSubscriptionByMemberUseCase useCase;

    @Test
    @DisplayName("Debería retornar la suscripción activa del alumno")
    void shouldReturnActiveSubscription() {
        // Retornamos un Member mockeado
        com.trainingapp.trainingapp.domain.entity.user.Member mockMember = mock(com.trainingapp.trainingapp.domain.entity.user.Member.class);
        when(memberAccessValidator.findMemberAndValidateAccess(100L)).thenReturn(mockMember);

        Subscription mockSub = mock(Subscription.class);
        when(subscriptionRepository.findActiveByMemberId(100L)).thenReturn(Optional.of(mockSub));
        when(subscriptionDTOMapper.toResponse(mockSub)).thenReturn(mock(SubscriptionResponse.class));

        SubscriptionResponse response = useCase.execute(100L);
        assertNotNull(response);
    }

    @Test
    @DisplayName("Debería lanzar error de negocio si el alumno NO tiene suscripción activa")
    void shouldThrowExceptionWhenNotActive() {
        // Retornamos un Member mockeado
        com.trainingapp.trainingapp.domain.entity.user.Member mockMember = mock(com.trainingapp.trainingapp.domain.entity.user.Member.class);
        when(memberAccessValidator.findMemberAndValidateAccess(100L)).thenReturn(mockMember);

        when(subscriptionRepository.findActiveByMemberId(100L)).thenReturn(Optional.empty());

        assertThrows(ActiveSubscriptionNotFoundException.class, () -> useCase.execute(100L));
    }
}