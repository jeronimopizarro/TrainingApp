package com.trainingapp.trainingapp.application.validator;

import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.exception.user.member.MemberNotFoundException;
import com.trainingapp.trainingapp.domain.repository.user.MemberRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberAccessValidatorTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private MemberAccessValidator validator;

    @Test
    @DisplayName("Debería encontrar al miembro y validar su gimnasio")
    void shouldFindMemberAndValidateGym() {
        Member mockMember = mock(Member.class);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(mockMember));
        when(mockMember.getGymId()).thenReturn(10L);

        doNothing().when(securityUtils).validateSameGym(10L);

        assertDoesNotThrow(() -> validator.findMemberAndValidateAccess(1L));
        verify(securityUtils).validateSameGym(10L);
    }

    @Test
    @DisplayName("Debería lanzar error si el miembro no existe")
    void shouldThrowIfMemberDoesNotExist() {
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> validator.findMemberAndValidateAccess(1L));
        verify(securityUtils, never()).validateSameGym(any());
    }
}