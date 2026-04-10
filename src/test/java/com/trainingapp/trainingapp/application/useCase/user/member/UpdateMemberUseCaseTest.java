package com.trainingapp.trainingapp.application.useCase.user.member;

import com.trainingapp.trainingapp.application.mapper.member.MemberDTOMapper;
import com.trainingapp.trainingapp.application.validator.UserAccessValidator;
import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.exception.user.member.MemberNotFoundException;
import com.trainingapp.trainingapp.domain.repository.user.MemberRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.user.member.MemberResponse;
import com.trainingapp.trainingapp.web.dto.user.member.UpdateMemberRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateMemberUseCaseTest {

    @Mock private MemberRepository memberRepository;
    @Mock private SecurityUtils securityUtils;
    @Mock private MemberDTOMapper memberDTOMapper;
    @Mock private UserAccessValidator userAccessValidator;

    @InjectMocks private UpdateMemberUseCase useCase;

    @Test
    @DisplayName("Debería actualizar un miembro exitosamente")
    void shouldUpdateMemberSuccessfully() {
        Long memberId = 1L;
        Long gymId = 10L;
        LocalDate newBirthDate = LocalDate.of(1990, 1, 1);

        UpdateMemberRequest request = new UpdateMemberRequest(
                "Carlos", "Gomez", "87654321", newBirthDate, "Definición"
        );

        Member mockMember = mock(Member.class);

        // Buscar miembro
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        when(mockMember.getGymId()).thenReturn(gymId);
        when(mockMember.getId()).thenReturn(memberId);

        // Validaciones de seguridad
        doNothing().when(securityUtils).validateSameGym(gymId);
        doNothing().when(userAccessValidator).validateWritePermission(memberId);

        when(memberRepository.save(mockMember)).thenReturn(mockMember);
        when(memberDTOMapper.toResponse(mockMember)).thenReturn(mock(MemberResponse.class));

        MemberResponse response = useCase.execute(memberId, request);

        assertNotNull(response);

        // Verificamos que se llamó al método de actualización interno de la entidad
        verify(mockMember).updateMemberDetails("Carlos", "Gomez", "87654321", newBirthDate, "Definición");
        verify(memberRepository).save(mockMember);
    }

    @Test
    @DisplayName("Debería lanzar MemberNotFoundException si el miembro no existe")
    void shouldThrowExceptionWhenMemberNotFound() {
        Long memberId = 99L;
        UpdateMemberRequest request = new UpdateMemberRequest("Carlos", "Gomez", "87654321", LocalDate.now(), "Definición");

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> useCase.execute(memberId, request));

        verifyNoInteractions(securityUtils);
        verifyNoInteractions(userAccessValidator);
    }
}