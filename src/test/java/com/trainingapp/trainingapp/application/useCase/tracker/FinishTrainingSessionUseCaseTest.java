package com.trainingapp.trainingapp.application.useCase.tracker;

import com.trainingapp.trainingapp.application.mapper.tracker.TrainingSessionDTOMapper;
import com.trainingapp.trainingapp.domain.entity.tracker.TrainingSession;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.exception.tracker.UnauthorizedSessionAccessException;
import com.trainingapp.trainingapp.domain.repository.tracker.TrainingSessionRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.tracker.SessionResponse;
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
class FinishTrainingSessionUseCaseTest {

    @Mock private TrainingSessionRepository trainingSessionRepository;
    @Mock private SecurityUtils securityUtils;
    @Mock private TrainingSessionDTOMapper trainingSessionDTOMapper;

    @InjectMocks private FinishTrainingSessionUseCase useCase;

    @Test
    @DisplayName("Debería finalizar la sesión exitosamente si el usuario es el dueño")
    void shouldFinishSessionSuccessfully() {
        User mockUser = mock(User.class);
        when(securityUtils.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getId()).thenReturn(100L);

        TrainingSession mockSession = mock(TrainingSession.class);
        when(trainingSessionRepository.findById(1L)).thenReturn(Optional.of(mockSession));
        when(mockSession.getMemberId()).thenReturn(100L);

        when(trainingSessionDTOMapper.toResponse(mockSession)).thenReturn(mock(SessionResponse.class));

        SessionResponse response = useCase.execute(1L);

        assertNotNull(response);
        verify(mockSession).finish();
        verify(trainingSessionRepository).save(mockSession);
    }

    @Test
    @DisplayName("Debería lanzar UnauthorizedSessionAccessException si no es el dueño")
    void shouldThrowExceptionWhenNotOwner() {
        User mockUser = mock(User.class);
        when(securityUtils.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getId()).thenReturn(100L); // Usuario actual

        TrainingSession mockSession = mock(TrainingSession.class);
        when(trainingSessionRepository.findById(1L)).thenReturn(Optional.of(mockSession));
        when(mockSession.getMemberId()).thenReturn(999L); // Dueño diferente

        assertThrows(UnauthorizedSessionAccessException.class, () -> useCase.execute(1L));
        verify(mockSession, never()).finish();
    }
}