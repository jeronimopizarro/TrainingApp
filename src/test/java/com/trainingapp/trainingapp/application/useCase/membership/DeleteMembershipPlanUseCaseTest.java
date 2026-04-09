package com.trainingapp.trainingapp.application.useCase.membership;

import com.trainingapp.trainingapp.domain.entity.membership.MembershipPlan;
import com.trainingapp.trainingapp.domain.repository.membership.MembershipPlanRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteMembershipPlanUseCaseTest {

    @Mock private MembershipPlanRepository planRepository;
    @Mock private SecurityUtils securityUtils;

    @InjectMocks private DeleteMembershipPlanUseCase useCase;

    @Test
    @DisplayName("Debería desactivar el plan de membresía exitosamente")
    void shouldDeactivatePlanSuccessfully() {
        Long planId = 1L;
        Long gymId = 10L;
        MembershipPlan mockPlan = mock(MembershipPlan.class);

        when(planRepository.findById(planId)).thenReturn(Optional.of(mockPlan));
        when(mockPlan.getGymId()).thenReturn(gymId);

        doNothing().when(securityUtils).validateSameGym(gymId);

        useCase.execute(planId);

        verify(mockPlan).deactivate();
        verify(planRepository).save(mockPlan);
    }
}