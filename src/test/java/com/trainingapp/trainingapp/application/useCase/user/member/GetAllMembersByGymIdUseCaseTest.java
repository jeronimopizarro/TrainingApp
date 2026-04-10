package com.trainingapp.trainingapp.application.useCase.user.member;

import com.trainingapp.trainingapp.application.mapper.member.MemberDTOMapper;
import com.trainingapp.trainingapp.application.validator.GymValidator;
import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.repository.user.MemberRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.user.member.MemberResponse;
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
class GetAllMembersByGymIdUseCaseTest {

    @Mock private MemberRepository memberRepository;
    @Mock private SecurityUtils securityUtils;
    @Mock private MemberDTOMapper memberDTOMapper;
    @Mock private GymValidator gymValidator;

    @InjectMocks private GetAllMembersByGymIdUseCase useCase;

    @Test
    @DisplayName("Debería retornar todos los miembros de un gimnasio específico")
    void shouldReturnAllMembersForGym() {
        Long gymId = 10L;
        Member mockMember = mock(Member.class);

        // 1. Validaciones
        doNothing().when(gymValidator).validateExists(gymId);
        doNothing().when(securityUtils).validateSameGym(gymId);

        // 2. Buscar en repositorio
        when(memberRepository.findByGymId(gymId)).thenReturn(List.of(mockMember));

        // 3. Mapear a respuesta
        when(memberDTOMapper.toResponse(mockMember)).thenReturn(mock(MemberResponse.class));

        List<MemberResponse> response = useCase.execute(gymId);

        assertEquals(1, response.size());
        verify(gymValidator).validateExists(gymId);
        verify(securityUtils).validateSameGym(gymId);
        verify(memberRepository).findByGymId(gymId);
    }
}