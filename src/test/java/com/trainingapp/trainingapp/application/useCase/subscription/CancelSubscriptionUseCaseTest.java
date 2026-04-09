package com.trainingapp.trainingapp.application.useCase.subscription;

import com.trainingapp.trainingapp.application.mapper.subscription.SubscriptionDTOMapper;
import com.trainingapp.trainingapp.application.validator.MemberAccessValidator;
import com.trainingapp.trainingapp.domain.entity.subscription.Subscription;
import com.trainingapp.trainingapp.domain.enums.subscription.SubscriptionStatus;
import com.trainingapp.trainingapp.domain.exception.subscription.SubscriptionNotFoundException;
import com.trainingapp.trainingapp.domain.repository.subscription.SubscriptionRepository;
import com.trainingapp.trainingapp.web.dto.subscription.SubscriptionResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CancelSubscriptionUseCaseTest {

    @Mock private SubscriptionRepository subscriptionRepository;
    @Mock private SubscriptionDTOMapper subscriptionDTOMapper;
    @Mock private MemberAccessValidator memberAccessValidator;

    @InjectMocks private CancelSubscriptionUseCase useCase;

    @Test
    @DisplayName("Debería cancelar la suscripción, cambiar estado a CANCELLED y guardar")
    void shouldCancelSubscription() {
        Subscription mockSub = Subscription.restore(1L, 100L, 1L, "Mensual", LocalDate.now(), LocalDate.now().plusMonths(1), SubscriptionStatus.ACTIVE);
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(mockSub));

        // Retornamos un Member mockeado
        com.trainingapp.trainingapp.domain.entity.user.Member mockMember = mock(com.trainingapp.trainingapp.domain.entity.user.Member.class);
        when(memberAccessValidator.findMemberAndValidateAccess(100L)).thenReturn(mockMember);

        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(mockSub);
        SubscriptionResponse fakeResponse = mock(SubscriptionResponse.class);
        when(subscriptionDTOMapper.toResponse(mockSub)).thenReturn(fakeResponse);

        useCase.execute(1L);

        assertEquals(SubscriptionStatus.CANCELLED, mockSub.getStatus(), "El estado de la entidad debe ser CANCELLED");
        verify(subscriptionRepository).save(mockSub);
    }

    @Test
    @DisplayName("Debería lanzar error 404 si la suscripción no existe")
    void shouldThrowExceptionWhenNotFound() {
        when(subscriptionRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(SubscriptionNotFoundException.class, () -> useCase.execute(99L));
    }
}