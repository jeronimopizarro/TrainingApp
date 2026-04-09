package com.trainingapp.trainingapp.application.useCase.membership;

import com.trainingapp.trainingapp.application.mapper.membershipPlan.MembershipPlanDTOMapper;
import com.trainingapp.trainingapp.domain.entity.membership.MembershipPlan;
import com.trainingapp.trainingapp.domain.exception.membership.DuplicateMembershipPlanNameException;
import com.trainingapp.trainingapp.domain.repository.membership.MembershipPlanRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.membership.MembershipPlanResponse;
import com.trainingapp.trainingapp.web.dto.membership.UpdateMembershipPlanRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateMembershipPlanUseCaseTest {

    @Mock private MembershipPlanRepository planRepository;
    @Mock private SecurityUtils securityUtils;
    @Mock private MembershipPlanDTOMapper membershipPlanDTOMapper;

    @InjectMocks private UpdateMembershipPlanUseCase useCase;

    @Test
    @DisplayName("Debería actualizar los detalles del plan exitosamente")
    void shouldUpdatePlanSuccessfully() {
        Long planId = 1L;
        Long gymId = 10L;
        UpdateMembershipPlanRequest request = new UpdateMembershipPlanRequest(
                "Pase VIP", "VIP", new BigDecimal("20000.0"), 12
        );
        MembershipPlan mockPlan = mock(MembershipPlan.class);

        when(planRepository.findById(planId)).thenReturn(Optional.of(mockPlan));
        when(mockPlan.getGymId()).thenReturn(gymId);

        doNothing().when(securityUtils).validateSameGym(gymId);
        when(planRepository.existsByNameAndGymIdAndIdNot("Pase VIP", gymId, planId)).thenReturn(false);

        when(planRepository.save(mockPlan)).thenReturn(mockPlan);
        when(membershipPlanDTOMapper.toResponse(mockPlan)).thenReturn(mock(MembershipPlanResponse.class));

        MembershipPlanResponse response = useCase.execute(planId, request);

        assertNotNull(response);
        verify(mockPlan).updateDetails("Pase VIP", "VIP", new BigDecimal("20000.0"), 12);
        verify(planRepository).save(mockPlan);
    }

    @Test
    @DisplayName("Debería lanzar DuplicateMembershipPlanNameException si el nombre ya está en uso por otro plan")
    void shouldThrowExceptionWhenDuplicateName() {
        Long planId = 1L;
        Long gymId = 10L;
        UpdateMembershipPlanRequest request = new UpdateMembershipPlanRequest(
                "Pase VIP", "VIP", new BigDecimal("20000.0"), 12
        );
        MembershipPlan mockPlan = mock(MembershipPlan.class);

        when(planRepository.findById(planId)).thenReturn(Optional.of(mockPlan));
        when(mockPlan.getGymId()).thenReturn(gymId);

        doNothing().when(securityUtils).validateSameGym(gymId);

        // Simulamos que el nombre ya existe
        when(planRepository.existsByNameAndGymIdAndIdNot("Pase VIP", gymId, planId)).thenReturn(true);

        assertThrows(DuplicateMembershipPlanNameException.class, () -> useCase.execute(planId, request));
        verify(mockPlan, never()).updateDetails(anyString(), anyString(), any(), anyInt());
        verify(planRepository, never()).save(any());
    }
}