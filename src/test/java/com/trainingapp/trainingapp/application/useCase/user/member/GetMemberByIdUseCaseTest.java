package com.trainingapp.trainingapp.application.useCase.user.member;

import com.trainingapp.trainingapp.application.mapper.member.MemberDTOMapper;
import com.trainingapp.trainingapp.application.validator.UserAccessValidator;
import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.exception.user.member.MemberNotFoundException;
import com.trainingapp.trainingapp.domain.repository.user.MemberRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.user.member.MemberResponse;
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
class GetMemberByIdUseCaseTest {

    @Mock private MemberRepository memberRepository;
    @Mock private SecurityUtils securityUtils;
    @Mock private MemberDTOMapper memberDTOMapper;
    @Mock private UserAccessValidator userAccessValidator;

    @InjectMocks private GetMemberByIdUseCase useCase;

    @Test
    @DisplayName("Debería retornar un miembro por su ID exitosamente")
    void shouldReturnMemberByIdSuccessfully() {
        Long memberId = 1L;
        Long gymId = 10L;
        Member mockMember = mock(Member.class);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(mockMember.getGymId()).thenReturn(gymId);

        doNothing().when(securityUtils).validateSameGym(gymId);
        doNothing().when(userAccessValidator).validateReadPermission(mockMember);

        when(memberDTOMapper.toResponse(mockMember)).thenReturn(mock(MemberResponse.class));

        MemberResponse response = useCase.execute(memberId);

        assertNotNull(response);
        verify(securityUtils).validateSameGym(gymId);
        verify(userAccessValidator).validateReadPermission(mockMember);
    }

    @Test
    @DisplayName("Debería lanzar MemberNotFoundException si el miembro no existe")
    void shouldThrowExceptionWhenMemberNotFound() {
        Long memberId = 99L;

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> useCase.execute(memberId));

        verifyNoInteractions(securityUtils);
        verifyNoInteractions(userAccessValidator);
        verifyNoInteractions(memberDTOMapper);
    }
}