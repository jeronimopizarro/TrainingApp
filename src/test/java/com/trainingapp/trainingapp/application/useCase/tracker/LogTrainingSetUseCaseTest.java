package com.trainingapp.trainingapp.application.useCase.tracker;

import com.trainingapp.trainingapp.application.mapper.tracker.TrainingSessionDTOMapper;
import com.trainingapp.trainingapp.domain.entity.tracker.SetLog;
import com.trainingapp.trainingapp.domain.entity.tracker.TrainingSession;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.repository.tracker.TrainingSessionRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.tracker.LogSetRequest;
import com.trainingapp.trainingapp.web.dto.tracker.SetLogResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogTrainingSetUseCaseTest {

    @Mock private TrainingSessionRepository trainingSessionRepository;
    @Mock private SecurityUtils securityUtils;
    @Mock private TrainingSessionDTOMapper trainingSessionDTOMapper;

    @InjectMocks private LogTrainingSetUseCase useCase;

    @Test
    @DisplayName("Debería registrar una serie en la sesión si es el dueño")
    void shouldLogSetSuccessfully() {
        // Arrange
        LogSetRequest request = new LogSetRequest(5L, 1, 10, new BigDecimal("50.0"), 2, "Al fallo");

        User mockUser = mock(User.class);
        when(securityUtils.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getId()).thenReturn(100L);

        TrainingSession mockSession = mock(TrainingSession.class);
        when(trainingSessionRepository.findById(1L)).thenReturn(Optional.of(mockSession));
        when(mockSession.getMemberId()).thenReturn(100L);

        // Simulamos la grabación y el retorno de las series
        when(trainingSessionRepository.save(mockSession)).thenReturn(mockSession);
        SetLog mockSetLog = mock(SetLog.class);
        when(mockSession.getSets()).thenReturn(List.of(mockSetLog)); // Simulamos que tiene al menos 1 elemento

        when(trainingSessionDTOMapper.toSetLogResponse(mockSetLog)).thenReturn(mock(SetLogResponse.class));

        // Act
        SetLogResponse response = useCase.execute(1L, request);

        // Assert
        assertNotNull(response);
        verify(mockSession).recordSet(5L, 10, new BigDecimal("50.0"), 2, "Al fallo");
        verify(trainingSessionRepository).save(mockSession);
    }
}