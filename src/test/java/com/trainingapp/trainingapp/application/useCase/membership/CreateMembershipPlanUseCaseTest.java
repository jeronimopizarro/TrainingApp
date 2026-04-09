package com.trainingapp.trainingapp.application.useCase.membership;

import com.trainingapp.trainingapp.application.mapper.membershipPlan.MembershipPlanDTOMapper;
import com.trainingapp.trainingapp.application.validator.GymValidator;
import com.trainingapp.trainingapp.domain.entity.membership.MembershipPlan;
import com.trainingapp.trainingapp.domain.exception.membership.DuplicateMembershipPlanNameException;
import com.trainingapp.trainingapp.domain.repository.membership.MembershipPlanRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.membership.CreateMembershipPlanRequest;
import com.trainingapp.trainingapp.web.dto.membership.MembershipPlanResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateMembershipPlanUseCaseTest {

    @Mock private MembershipPlanRepository planRepository;
    @Mock private SecurityUtils securityUtils;
    @Mock private MembershipPlanDTOMapper membershipPlanDTOMapper;
    @Mock private GymValidator gymValidator;

    @InjectMocks private CreateMembershipPlanUseCase useCase;

    @Test
    @DisplayName("Debería crear un plan de membresía exitosamente")
    void shouldCreateMembershipPlanSuccessfully() {
        Long gymId = 10L;
        CreateMembershipPlanRequest request = new CreateMembershipPlanRequest(
                "Pase Libre", "Full", new BigDecimal("15000.0"), 1, gymId
        );
        MembershipPlan mockPlan = mock(MembershipPlan.class);
        MembershipPlan savedPlan = mock(MembershipPlan.class);

        doNothing().when(gymValidator).validateExists(gymId);
        doNothing().when(securityUtils).validateSameGym(gymId);

        when(planRepository.existsByNameAndGymId("Pase Libre", gymId)).thenReturn(false);
        when(membershipPlanDTOMapper.toDomain(request)).thenReturn(mockPlan);
        when(planRepository.save(mockPlan)).thenReturn(savedPlan);
        when(membershipPlanDTOMapper.toResponse(savedPlan)).thenReturn(mock(MembershipPlanResponse.class));

        MembershipPlanResponse response = useCase.execute(request);

        assertNotNull(response);
        verify(planRepository).save(mockPlan);
    }

    @Test
    @DisplayName("Debería lanzar DuplicateMembershipPlanNameException si el nombre ya existe")
    void shouldThrowExceptionWhenNameAlreadyExists() {
        Long gymId = 10L;
        CreateMembershipPlanRequest request = new CreateMembershipPlanRequest(
                "Pase Libre", "Full", new BigDecimal("15000.0"), 1, gymId
        );

        doNothing().when(gymValidator).validateExists(gymId);
        doNothing().when(securityUtils).validateSameGym(gymId);

        when(planRepository.existsByNameAndGymId("Pase Libre", gymId)).thenReturn(true);

        assertThrows(DuplicateMembershipPlanNameException.class, () -> useCase.execute(request));
        verify(planRepository, never()).save(any());
        verifyNoInteractions(membershipPlanDTOMapper);
    }
}