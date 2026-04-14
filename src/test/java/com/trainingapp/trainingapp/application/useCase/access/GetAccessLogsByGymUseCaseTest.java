package com.trainingapp.trainingapp.application.useCase.access;

import com.trainingapp.trainingapp.application.mapper.access.AccessLogDTOMapper;
import com.trainingapp.trainingapp.domain.entity.Access.AccessLog;
import com.trainingapp.trainingapp.domain.repository.Access.AccessLogRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.access.GymAccessSummaryResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAccessLogsByGymUseCaseTest {

    @Mock private AccessLogRepository accessLogRepository;
    @Mock private SecurityUtils securityUtils;
    @Mock private AccessLogDTOMapper accessLogDTOMapper;

    @InjectMocks private GetAccessLogsByGymUseCase useCase;

    @Test
    @DisplayName("Debería retornar un resumen de accesos del gimnasio para el día de hoy")
    void shouldReturnGymAccessSummary() {
        Long gymId = 10L;

        // Simular dos registros, uno exitoso de hoy y uno fallido de ayer
        AccessLog logSuccessToday = mock(AccessLog.class);
        when(logSuccessToday.getTimestamp()).thenReturn(LocalDateTime.now());
        when(logSuccessToday.isAccessGranted()).thenReturn(true);

        AccessLog logFailedYesterday = mock(AccessLog.class);
        when(logFailedYesterday.getTimestamp()).thenReturn(LocalDateTime.now().minusDays(1));

        List<AccessLog> logs = List.of(logSuccessToday, logFailedYesterday);

        when(securityUtils.getCurrentUserGymId()).thenReturn(gymId);
        when(accessLogRepository.findByGymId(gymId)).thenReturn(logs);

        GymAccessSummaryResponse mockResponse = mock(GymAccessSummaryResponse.class);
        // Esperamos que se hayan calculado 1 acceso exitoso y 0 fallidos para el día de hoy
        when(accessLogDTOMapper.toGymSummaryResponse(1L, 0L, logs)).thenReturn(mockResponse);

        GymAccessSummaryResponse response = useCase.execute(null);

        assertNotNull(response);
        verify(accessLogRepository).findByGymId(gymId);
        verify(accessLogDTOMapper).toGymSummaryResponse(1L, 0L, logs);
    }
}