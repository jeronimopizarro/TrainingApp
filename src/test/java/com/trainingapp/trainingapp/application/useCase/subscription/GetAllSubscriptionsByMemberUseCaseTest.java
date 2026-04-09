package com.trainingapp.trainingapp.application.useCase.subscription;

import com.trainingapp.trainingapp.application.mapper.subscription.SubscriptionDTOMapper;
import com.trainingapp.trainingapp.application.validator.MemberAccessValidator;
import com.trainingapp.trainingapp.domain.entity.subscription.Subscription;
import com.trainingapp.trainingapp.domain.repository.subscription.SubscriptionRepository;
import com.trainingapp.trainingapp.web.dto.subscription.SubscriptionResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllSubscriptionsByMemberUseCaseTest {

    @Mock private SubscriptionRepository subscriptionRepository;
    @Mock private SubscriptionDTOMapper subscriptionDTOMapper;
    @Mock private MemberAccessValidator memberAccessValidator;

    @InjectMocks private GetAllSubscriptionsByMemberUseCase useCase;

    @Test
    @DisplayName("Debería retornar el historial completo de suscripciones de un alumno")
    void shouldReturnAllSubscriptions() {
        // Retornamos un Member mockeado
        com.trainingapp.trainingapp.domain.entity.user.Member mockMember = mock(com.trainingapp.trainingapp.domain.entity.user.Member.class);
        when(memberAccessValidator.findMemberAndValidateAccess(100L)).thenReturn(mockMember);

        Subscription mockSub = mock(Subscription.class);
        when(subscriptionRepository.findAllByMemberIdOrderByStartDateDesc(100L)).thenReturn(List.of(mockSub));
        when(subscriptionDTOMapper.toResponse(mockSub)).thenReturn(mock(SubscriptionResponse.class));

        List<SubscriptionResponse> responses = useCase.execute(100L);

        assertEquals(1, responses.size(), "Debe retornar una lista mapeada de suscripciones");
    }
}