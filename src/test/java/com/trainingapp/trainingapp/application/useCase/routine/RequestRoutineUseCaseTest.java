package com.trainingapp.trainingapp.application.useCase.routine;

import com.trainingapp.trainingapp.domain.entity.routine.RoutineRequest;
import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.routine.ExperienceLevel;
import com.trainingapp.trainingapp.domain.enums.routine.RoutineRequestStatus;
import com.trainingapp.trainingapp.domain.exception.routine.ActiveRoutineRequestAlreadyExistsException;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRequestRepository;
import com.trainingapp.trainingapp.domain.repository.user.MemberRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.routine.RequestRoutineMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestRoutineUseCaseTest {

    @Mock private RoutineRequestRepository routineRequestRepository;
    @Mock private MemberRepository memberRepository;
    @Mock private SecurityUtils securityUtils;

    @InjectMocks private RequestRoutineUseCase useCase;

    @Test
    @DisplayName("Debería guardar una nueva solicitud si el alumno no tiene solicitudes previas")
    void shouldCreateRequestSuccessfully() {
        RequestRoutineMessage message = new RequestRoutineMessage(
                null, 3, ExperienceLevel.BEGINNER, "Ninguna", "Fuerza"
        );
        User mockUser = mock(User.class);
        Member mockMember = mock(Member.class);

        // Obtenemos el ID del usuario
        when(securityUtils.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getId()).thenReturn(100L);

        // Validar que NO exista solicitud pendiente
        when(routineRequestRepository.existsByMemberIdAndStatus(100L, RoutineRequestStatus.PENDING))
                .thenReturn(false);

        // Buscar al miembro
        when(memberRepository.findById(100L)).thenReturn(Optional.of(mockMember));
        when(mockMember.getId()).thenReturn(100L);
        when(mockMember.getGymId()).thenReturn(10L);

        useCase.execute(message);

        verify(routineRequestRepository).save(any(RoutineRequest.class));
    }

    @Test
    @DisplayName("Debería lanzar error si el alumno YA TIENE una solicitud pendiente")
    void shouldThrowExceptionWhenRequestAlreadyExists() {
        RequestRoutineMessage message = new RequestRoutineMessage(
                null, 3, ExperienceLevel.BEGINNER, "", ""
        );
        User mockUser = mock(User.class);

        when(securityUtils.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getId()).thenReturn(100L);

        when(routineRequestRepository.existsByMemberIdAndStatus(100L, RoutineRequestStatus.PENDING))
                .thenReturn(true);

        assertThrows(ActiveRoutineRequestAlreadyExistsException.class, () -> useCase.execute(message));
        verify(routineRequestRepository, never()).save(any());
    }
}