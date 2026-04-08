package com.trainingapp.trainingapp.application.useCase.dashboard;

import com.trainingapp.trainingapp.domain.entity.routine.RoutineRequest;
import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.routine.ExperienceLevel;
import com.trainingapp.trainingapp.domain.enums.routine.RoutineRequestStatus;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRequestRepository;
import com.trainingapp.trainingapp.domain.repository.user.MemberRepository;
import com.trainingapp.trainingapp.domain.repository.user.TrainerRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.dashboard.TrainerDashboardResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerDashboardUseCaseTest {

    @Mock private SecurityUtils securityUtils;
    @Mock private TrainerRepository trainerRepository;
    @Mock private RoutineRequestRepository routineRequestRepository;
    @Mock private MemberRepository memberRepository;

    @InjectMocks private TrainerDashboardUseCase useCase;

    @Test
    @DisplayName("Debería retornar las solicitudes pendientes del gimnasio del entrenador logueado")
    void shouldReturnPendingRequestsForTrainerGym() {
        // 1. Arrange: Simulamos Profe con ID 2L en Gym 10L
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(2L);
        when(securityUtils.getCurrentUser()).thenReturn(mockUser);

        Trainer mockTrainer = mock(Trainer.class);
        when(mockTrainer.getGymId()).thenReturn(10L);
        when(trainerRepository.findById(2L)).thenReturn(Optional.of(mockTrainer));

        // 2. Simulamos una solicitud pendiente de un alumno (ID 100L)
        RoutineRequest request = RoutineRequest.restore(
                1L, 100L, 10L, LocalDateTime.now(), RoutineRequestStatus.PENDING,
                null, null, null, 3, ExperienceLevel.BEGINNER, "Ninguna", "Fuerza"
        );
        when(routineRequestRepository.findByGymIdAndStatus(10L, RoutineRequestStatus.PENDING))
                .thenReturn(List.of(request));

        // 3. Simulamos los datos del alumno para el mapeo del nombre
        Member mockMember = mock(Member.class);
        when(mockMember.getFirstName()).thenReturn("Lionel");
        when(mockMember.getLastName()).thenReturn("Messi");
        when(memberRepository.findById(100L)).thenReturn(Optional.of(mockMember));

        // Act
        TrainerDashboardResponse response = useCase.execute();

        // Assert
        assertEquals(1, response.pendingRequests().size());
        assertEquals("Lionel Messi", response.pendingRequests().get(0).memberFullName());
        assertEquals("Fuerza", response.pendingRequests().get(0).primaryGoal());
    }
}