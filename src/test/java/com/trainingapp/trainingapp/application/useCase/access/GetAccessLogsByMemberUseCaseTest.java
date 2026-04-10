package com.trainingapp.trainingapp.application.useCase.access;

import com.trainingapp.trainingapp.application.mapper.access.AccessLogDTOMapper;
import com.trainingapp.trainingapp.domain.entity.Access.AccessLog;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.repository.Access.AccessLogRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.access.MemberAccessSummaryResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAccessLogsByMemberUseCaseTest {

    @Mock private AccessLogRepository accessLogRepository;
    @Mock private SecurityUtils securityUtils;
    @Mock private AccessLogDTOMapper accessLogDTOMapper;

    @InjectMocks private GetAccessLogsByMemberUseCase useCase;

    @Test
    @DisplayName("Debería retornar el resumen de accesos del miembro actual")
    void shouldReturnMemberAccessSummary() {
        Long currentUserId = 100L;
        User mockUser = mock(User.class);

        // Usuario actual
        when(securityUtils.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getId()).thenReturn(currentUserId);

        // Simulamos dos registros, uno exitoso y uno fallido
        AccessLog logSuccess = mock(AccessLog.class);
        when(logSuccess.isAccessGranted()).thenReturn(true);

        AccessLog logFailed = mock(AccessLog.class);
        when(logFailed.isAccessGranted()).thenReturn(false);

        List<AccessLog> logs = List.of(logSuccess, logFailed);

        when(accessLogRepository.findByMemberId(currentUserId)).thenReturn(logs);

        MemberAccessSummaryResponse mockResponse = mock(MemberAccessSummaryResponse.class);
        // Esperamos que se haya calculado 1 visita exitosa total
        when(accessLogDTOMapper.toMemberSummaryResponse(1L, logs)).thenReturn(mockResponse);

        MemberAccessSummaryResponse response = useCase.execute();

        assertNotNull(response);
        verify(accessLogRepository).findByMemberId(currentUserId);
        verify(accessLogDTOMapper).toMemberSummaryResponse(1L, logs);
    }
}