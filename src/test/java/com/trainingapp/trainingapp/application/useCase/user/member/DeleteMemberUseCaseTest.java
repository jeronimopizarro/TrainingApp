package com.trainingapp.trainingapp.application.useCase.user.member;

import com.trainingapp.trainingapp.application.validator.UserAccessValidator;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteMemberUseCaseTest {

    @Mock private MemberRepository memberRepository;
    @Mock private SecurityUtils securityUtils;
    @Mock private UserAccessValidator userAccessValidator;

    @InjectMocks private DeleteMemberUseCase useCase;

    @Test
    @DisplayName("Debería desactivar un miembro exitosamente")
    void shouldDeactivateMemberSuccessfully() {
        Long memberId = 1L;
        Long gymId = 10L;
        Member mockMember = mock(Member.class);

        // 1. Buscar miembro
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(mockMember.getGymId()).thenReturn(gymId);
        when(mockMember.getId()).thenReturn(memberId);

        // 2. Validaciones de seguridad
        doNothing().when(securityUtils).validateSameGym(gymId);
        doNothing().when(userAccessValidator).validateWritePermission(memberId);

        // Ejecutar
        useCase.execute(memberId);

        // Verificar que se haya desactivado y guardado
        verify(mockMember).deactivate();
        verify(memberRepository).save(mockMember);
    }

    @Test
    @DisplayName("Debería lanzar MemberNotFoundException si el miembro a eliminar no existe")
    void shouldThrowExceptionWhenDeletingNonExistentMember() {
        Long memberId = 99L;

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> useCase.execute(memberId));

        verifyNoInteractions(securityUtils);
        verifyNoInteractions(userAccessValidator);
        verify(memberRepository, never()).save(any());
    }
}