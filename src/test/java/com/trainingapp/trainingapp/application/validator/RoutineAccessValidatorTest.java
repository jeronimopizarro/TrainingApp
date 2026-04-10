package com.trainingapp.trainingapp.application.validator;

import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.exception.routine.UnauthorizedRoutineAccessException;
import com.trainingapp.trainingapp.domain.exception.routine.UnauthorizedRoutineModificationException;
import com.trainingapp.trainingapp.domain.repository.user.UserRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoutineAccessValidatorTest {

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RoutineAccessValidator validator;

    @Test
    @DisplayName("Member solo puede leer sus propias rutinas")
    void memberCanOnlyReadOwnRoutines() {
        User currentUser = mock(User.class);
        when(securityUtils.getCurrentUser()).thenReturn(currentUser);
        when(currentUser.isSuperAdmin()).thenReturn(false);
        when(currentUser.isMember()).thenReturn(true);
        when(currentUser.getId()).thenReturn(1L);

        Routine routine = mock(Routine.class);
        when(routine.getMemberId()).thenReturn(2L); // Rutina de otro

        assertThrows(UnauthorizedRoutineAccessException.class, () -> validator.validateReadPermission(routine));

        when(routine.getMemberId()).thenReturn(1L); // Su propia rutina
        assertDoesNotThrow(() -> validator.validateReadPermission(routine));
    }

    @Test
    @DisplayName("Trainer puede modificar rutinas que creó o le fueron asignadas")
    void trainerCanModifyAssignedOrCreatedRoutines() {
        User currentUser = mock(User.class);
        when(securityUtils.getCurrentUser()).thenReturn(currentUser);
        when(currentUser.isSuperAdmin()).thenReturn(false);
        when(currentUser.isMember()).thenReturn(false);
        when(currentUser.isTrainer()).thenReturn(true);
        when(currentUser.getId()).thenReturn(1L);

        Routine routine = mock(Routine.class);
        when(routine.getGymId()).thenReturn(10L);
        when(routine.getCreatedByUserId()).thenReturn(2L); // Creador diferente
        when(routine.getTrainerId()).thenReturn(2L); // Asignado a otro

        assertThrows(UnauthorizedRoutineModificationException.class, () -> validator.validateModificationPermission(routine));

        when(routine.getTrainerId()).thenReturn(1L); // Asignado a él
        assertDoesNotThrow(() -> validator.validateModificationPermission(routine));
    }
}