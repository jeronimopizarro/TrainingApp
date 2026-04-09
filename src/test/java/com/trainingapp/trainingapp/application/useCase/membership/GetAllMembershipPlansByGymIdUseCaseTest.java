package com.trainingapp.trainingapp.application.useCase.membership;

import com.trainingapp.trainingapp.application.mapper.membershipPlan.MembershipPlanDTOMapper;
import com.trainingapp.trainingapp.application.validator.GymValidator;
import com.trainingapp.trainingapp.domain.entity.membership.MembershipPlan;
import com.trainingapp.trainingapp.domain.repository.membership.MembershipPlanRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.membership.MembershipPlanResponse;
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
class GetAllMembershipPlansByGymIdUseCaseTest {

    @Mock private MembershipPlanRepository planRepository;
    @Mock private SecurityUtils securityUtils;
    @Mock private GymValidator gymValidator;
    @Mock private MembershipPlanDTOMapper membershipPlanDTOMapper;

    @InjectMocks private GetAllMembershipPlansByGymIdUseCase useCase;

    @Test
    @DisplayName("Debería retornar todos los planes de un gimnasio validado")
    void shouldReturnAllPlansForGym() {
        Long gymId = 10L;
        MembershipPlan mockPlan = mock(MembershipPlan.class);
        MembershipPlanResponse mockResponse = mock(MembershipPlanResponse.class);

        doNothing().when(gymValidator).validateExists(gymId);
        doNothing().when(securityUtils).validateSameGym(gymId);

        when(planRepository.findByGymId(gymId)).thenReturn(List.of(mockPlan));
        when(membershipPlanDTOMapper.toResponse(mockPlan)).thenReturn(mockResponse);

        List<MembershipPlanResponse> responses = useCase.execute(gymId);

        assertEquals(1, responses.size());
        verify(planRepository).findByGymId(gymId);
    }
}