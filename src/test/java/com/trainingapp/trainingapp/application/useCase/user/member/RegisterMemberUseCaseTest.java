package com.trainingapp.trainingapp.application.useCase.user.member;

import com.trainingapp.trainingapp.application.mapper.member.MemberDTOMapper;
import com.trainingapp.trainingapp.application.validator.UserRegistrationValidator;
import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.repository.user.MemberRepository;
import com.trainingapp.trainingapp.domain.repository.user.UserRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.user.member.MemberResponse;
import com.trainingapp.trainingapp.web.dto.user.member.RegisterMemberRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterMemberUseCaseTest {

    @Mock private MemberRepository memberRepository;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private SecurityUtils securityUtils;
    @Mock private MemberDTOMapper memberDTOMapper;
    @Mock private UserRegistrationValidator registrationValidator;

    @InjectMocks private RegisterMemberUseCase useCase;

    @Test
    @DisplayName("Debería registrar un nuevo miembro exitosamente")
    void shouldRegisterMemberSuccessfully() {
        Long gymId = 10L;
        RegisterMemberRequest request = new RegisterMemberRequest(
                "Juan", "Perez", "juan@test.com", "pass123", "12345678", gymId, LocalDate.of(1995, 5, 20), "Hipertrofia"
        );

        doNothing().when(securityUtils).validateSameGym(gymId);
        doNothing().when(registrationValidator).validateEmailIsUnique("juan@test.com");

        String encodedPassword = "encoded_password_123";
        when(passwordEncoder.encode("pass123")).thenReturn(encodedPassword);

        Member mockMember = mock(Member.class);
        when(memberDTOMapper.toDomain(request, encodedPassword)).thenReturn(mockMember);

        when(memberRepository.save(mockMember)).thenReturn(mockMember);
        when(memberDTOMapper.toResponse(mockMember)).thenReturn(mock(MemberResponse.class));

        MemberResponse response = useCase.execute(request);

        assertNotNull(response);
        verify(securityUtils).validateSameGym(gymId);
        verify(registrationValidator).validateEmailIsUnique("juan@test.com");
        verify(memberRepository).save(mockMember);
    }
}