package com.trainingapp.trainingapp.application.useCase.tracker;

import com.trainingapp.trainingapp.application.mapper.tracker.TrainingSessionDTOMapper;
import com.trainingapp.trainingapp.application.useCase.subscription.GetActiveSubscriptionByMemberUseCase;
import com.trainingapp.trainingapp.domain.entity.tracker.TrainingSession;
import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.exception.subscription.ActiveSubscriptionNotFoundException;
import com.trainingapp.trainingapp.domain.exception.tracker.ActiveSessionAlreadyExistsException;
import com.trainingapp.trainingapp.domain.exception.tracker.TrainingRequiresActiveSubscriptionException;
import com.trainingapp.trainingapp.domain.repository.tracker.TrainingSessionRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.subscription.SubscriptionResponse;
import com.trainingapp.trainingapp.web.dto.tracker.SessionResponse;
import com.trainingapp.trainingapp.web.dto.tracker.StartSessionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StartTrainingSessionUseCaseTest {

    @Mock
    private TrainingSessionRepository trainingSessionRepository;
    @Mock
    private GetActiveSubscriptionByMemberUseCase getActiveSubscriptionUseCase;
    @Mock
    private SecurityUtils securityUtils;
    @Mock
    private TrainingSessionDTOMapper trainingSessionDTOMapper;

    @InjectMocks
    private StartTrainingSessionUseCase useCase;

    private StartSessionRequest request;
    private Member mockMember;
    private final Long MEMBER_ID = 1L;
    private final Long GYM_ID = 10L;

    @BeforeEach
    void setUp() {
        request = new StartSessionRequest(100L, 5L); // Rutina 100, Día 5
        mockMember = mock(Member.class); // Creamos un socio falso
    }

    @Test
    @DisplayName("Debería iniciar sesión y guardar en BD si el alumno está al día y no tiene sesiones previas")
    void shouldStartSessionSuccessfully() {
        when(mockMember.getId()).thenReturn(MEMBER_ID);
        when(securityUtils.getCurrentUser()).thenReturn(mockMember);
        when(securityUtils.getCurrentUserGymId()).thenReturn(GYM_ID);

        when(trainingSessionRepository.findActiveSessionByMemberId(MEMBER_ID)).thenReturn(Optional.empty());

        TrainingSession mockSession = TrainingSession.startNew(MEMBER_ID, request.routineId(), request.trainingDayId(), GYM_ID);
        when(trainingSessionDTOMapper.toDomainStartSession(request, MEMBER_ID, GYM_ID)).thenReturn(mockSession);

        when(trainingSessionRepository.save(any(TrainingSession.class))).thenReturn(mockSession);
        when(trainingSessionDTOMapper.toResponse(any(TrainingSession.class))).thenReturn(mock(SessionResponse.class));

        SessionResponse response = useCase.execute(request);

        assertNotNull(response, "La respuesta no debería ser nula");

        verify(trainingSessionRepository, times(1)).save(any(TrainingSession.class));
    }

    @Test
    @DisplayName("Debería lanzar error y NO guardar nada si el alumno no tiene suscripción activa")
    void shouldThrowExceptionWhenSubscriptionNotActive() {
        when(mockMember.getId()).thenReturn(MEMBER_ID);
        when(securityUtils.getCurrentUser()).thenReturn(mockMember);
        when(securityUtils.getCurrentUserGymId()).thenReturn(GYM_ID);

        doThrow(new ActiveSubscriptionNotFoundException(MEMBER_ID))
                .when(getActiveSubscriptionUseCase).execute(MEMBER_ID);

        assertThrows(
                TrainingRequiresActiveSubscriptionException.class,
                () -> useCase.execute(request),
                "No debe dejar entrenar a un moroso"
        );

        verify(trainingSessionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar error y NO guardar nada si el alumno ya tiene una sesión abierta")
    void shouldThrowExceptionWhenActiveSessionAlreadyExists() {
        when(mockMember.getId()).thenReturn(MEMBER_ID);
        when(securityUtils.getCurrentUser()).thenReturn(mockMember);
        when(securityUtils.getCurrentUserGymId()).thenReturn(GYM_ID);


        SubscriptionResponse mockSubscription =
                mock(SubscriptionResponse.class);

        when(getActiveSubscriptionUseCase.execute(MEMBER_ID)).thenReturn(mockSubscription);

        TrainingSession oldSession = mock(TrainingSession.class);
        when(oldSession.getId()).thenReturn(999L);
        when(trainingSessionRepository.findActiveSessionByMemberId(MEMBER_ID)).thenReturn(Optional.of(oldSession));

        assertThrows(
                ActiveSessionAlreadyExistsException.class,
                () -> useCase.execute(request),
                "No debe dejar abrir dos sesiones a la vez"
        );

        verify(trainingSessionRepository, never()).save(any());
    }
}