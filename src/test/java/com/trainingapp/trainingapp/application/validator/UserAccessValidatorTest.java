package com.trainingapp.trainingapp.application.validator;

import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.domain.exception.user.UnauthorizedProfileAccessException;
import com.trainingapp.trainingapp.domain.exception.user.UnauthorizedProfileModificationException;
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
class UserAccessValidatorTest {

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private UserAccessValidator validator;

    @Test
    @DisplayName("SuperAdmin puede modificar a cualquiera")
    void superAdminCanModifyAnyone() {
        User currentUser = mock(User.class);
        when(securityUtils.getCurrentUser()).thenReturn(currentUser);
        when(currentUser.isSuperAdmin()).thenReturn(true);

        assertDoesNotThrow(() -> validator.validateWritePermission(2L));
    }

    @Test
    @DisplayName("Usuario normal solo puede modificarse a sí mismo")
    void normalUserCanOnlyModifySelf() {
        User currentUser = mock(User.class);
        when(securityUtils.getCurrentUser()).thenReturn(currentUser);
        when(currentUser.isSuperAdmin()).thenReturn(false);
        when(currentUser.isGymAdmin()).thenReturn(false);
        when(currentUser.getId()).thenReturn(1L);

        assertDoesNotThrow(() -> validator.validateWritePermission(1L));
        assertThrows(UnauthorizedProfileModificationException.class, () -> validator.validateWritePermission(2L));
    }

    @Test
    @DisplayName("Trainer puede leer perfil de Member")
    void trainerCanReadMemberProfile() {
        User currentUser = mock(User.class);
        when(securityUtils.getCurrentUser()).thenReturn(currentUser);
        when(currentUser.isSuperAdmin()).thenReturn(false);
        when(currentUser.isGymAdmin()).thenReturn(false);
        when(currentUser.getId()).thenReturn(1L);
        when(currentUser.isTrainer()).thenReturn(true);

        User targetUser = mock(User.class);
        when(targetUser.getId()).thenReturn(2L);
        when(targetUser.getRole()).thenReturn(Role.MEMBER);

        assertDoesNotThrow(() -> validator.validateReadPermission(targetUser));
    }

    @Test
    @DisplayName("Trainer no puede leer perfil de otro Trainer")
    void trainerCannotReadOtherTrainerProfile() {
        User currentUser = mock(User.class);
        when(securityUtils.getCurrentUser()).thenReturn(currentUser);
        when(currentUser.isSuperAdmin()).thenReturn(false);
        when(currentUser.isGymAdmin()).thenReturn(false);
        when(currentUser.getId()).thenReturn(1L);
        when(currentUser.isTrainer()).thenReturn(true);

        User targetUser = mock(User.class);
        when(targetUser.getId()).thenReturn(2L);
        when(targetUser.getRole()).thenReturn(Role.TRAINER);

        assertThrows(UnauthorizedProfileAccessException.class, () -> validator.validateReadPermission(targetUser));
    }
}