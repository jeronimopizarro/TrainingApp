package com.trainingapp.trainingapp.application.useCase.user.admin;

import com.trainingapp.trainingapp.application.validator.UserAccessValidator;
import com.trainingapp.trainingapp.domain.entity.user.Admin;
import com.trainingapp.trainingapp.domain.repository.user.AdminRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteAdminUseCaseTest {

    @Mock private AdminRepository adminRepository;
    @Mock private SecurityUtils securityUtils;
    @Mock private UserAccessValidator userAccessValidator;

    @InjectMocks private DeleteAdminUseCase useCase;

    @Test
    @DisplayName("Debería desactivar un administrador exitosamente")
    void shouldDeactivateAdmin() {
        Long id = 1L;
        Admin mockAdmin = mock(Admin.class);

        when(adminRepository.findById(id)).thenReturn(Optional.of(mockAdmin));
        when(mockAdmin.getGymId()).thenReturn(10L);
        when(mockAdmin.getId()).thenReturn(id);

        doNothing().when(securityUtils).validateSameGym(10L);
        doNothing().when(userAccessValidator).validateWritePermission(id);

        useCase.execute(id);

        verify(mockAdmin).deactivate();
        verify(adminRepository).save(mockAdmin);
    }
}