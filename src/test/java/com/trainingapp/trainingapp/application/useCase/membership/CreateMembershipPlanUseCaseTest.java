package com.trainingapp.trainingapp.application.useCase.membership;

import com.trainingapp.trainingapp.application.mapper.membershipPlan.MembershipPlanDTOMapper;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateMembershipPlanUseCaseTest {

    @Mock private MembershipPlanRepository planRepository;
    @Mock private SecurityUtils securityUtils;
    @Mock private MembershipPlanDTOMapper membershipPlanDTOMapper;

    @InjectMocks private CreateMembershipPlanUseCase useCase;

    @Test
    @DisplayName("Debería crear un plan de membresía exitosamente")
    void shouldCreateMembershipPlanSuccessfully() {
        Long gymId = 10L;
        CreateMembershipPlanRequest request = new CreateMembershipPlanRequest(
                "Pase Libre", "Full", new BigDecimal("15000.0"), 1, gymId
        );
        
        MembershipPlanResponse mockResponse = mock(MembershipPlanResponse.class);

        doNothing().when(securityUtils).validateSameGym(gymId);
        when(planRepository.existsByNameAndGymId("Pase Libre", gymId)).thenReturn(false);
        
        // En el UseCase actual, se llama a repository.save(any(MembershipPlan.class))
        // y el resultado se pasa al mapper.toResponse
        when(planRepository.save(any(MembershipPlan.class))).thenReturn(mock(MembershipPlan.class));
        when(membershipPlanDTOMapper.toResponse(any())).thenReturn(mockResponse);

        MembershipPlanResponse response = useCase.execute(request);

        assertNotNull(response);
        verify(planRepository).save(any(MembershipPlan.class));
        verify(membershipPlanDTOMapper).toResponse(any());
    }

    @Test
    @DisplayName("Debería lanzar DuplicateMembershipPlanNameException si el nombre ya existe")
    void shouldThrowExceptionWhenNameAlreadyExists() {
        Long gymId = 10L;
        CreateMembershipPlanRequest request = new CreateMembershipPlanRequest(
                "Pase Libre", "Full", new BigDecimal("15000.0"), 1, gymId
        );

        doNothing().when(securityUtils).validateSameGym(gymId);
        when(planRepository.existsByNameAndGymId("Pase Libre", gymId)).thenReturn(true);

        assertThrows(DuplicateMembershipPlanNameException.class, () -> useCase.execute(request));
        
        verify(planRepository, never()).save(any());
    }
}
