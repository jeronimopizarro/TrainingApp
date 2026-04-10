package com.trainingapp.trainingapp.application.useCase.user.admin;

import com.trainingapp.trainingapp.application.mapper.admin.AdminDTOMapper;
import com.trainingapp.trainingapp.application.validator.UserAccessValidator;
import com.trainingapp.trainingapp.domain.entity.user.Admin;
import com.trainingapp.trainingapp.domain.exception.user.AdminNotFoundException;
import com.trainingapp.trainingapp.domain.repository.user.AdminRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.user.admin.AdminResponse;
import com.trainingapp.trainingapp.web.dto.user.admin.UpdateAdminRequest;
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
class UpdateAdminUseCaseTest {

    @Mock private AdminRepository adminRepository;
    @Mock private SecurityUtils securityUtils;
    @Mock private AdminDTOMapper adminDTOMapper;
    @Mock private UserAccessValidator userAccessValidator;

    @InjectMocks private UpdateAdminUseCase useCase;

    @Test
    @DisplayName("Debería actualizar un admin exitosamente")
    void shouldUpdateAdminSuccessfully() {
        Long adminId = 1L;
        Long gymId = 10L;
        UpdateAdminRequest request = mock(UpdateAdminRequest.class);
        when(request.firstName()).thenReturn("Nuevo");
        when(request.lastName()).thenReturn("Nombre");
        when(request.dni()).thenReturn("12345678");

        Admin mockAdmin = mock(Admin.class);
        when(adminRepository.findById(adminId)).thenReturn(Optional.of(mockAdmin));
        when(mockAdmin.getGymId()).thenReturn(gymId);
        when(mockAdmin.getId()).thenReturn(adminId);

        doNothing().when(securityUtils).validateSameGym(gymId);
        doNothing().when(userAccessValidator).validateWritePermission(adminId);

        when(adminRepository.save(mockAdmin)).thenReturn(mockAdmin);
        when(adminDTOMapper.toResponse(mockAdmin)).thenReturn(mock(AdminResponse.class));

        AdminResponse response = useCase.execute(adminId, request);

        assertNotNull(response);
        verify(mockAdmin).updateBaseDetails("Nuevo", "Nombre", "12345678");
    }

    @Test
    @DisplayName("Debería lanzar error si el admin no existe")
    void shouldThrowExceptionWhenAdminNotFound() {
        Long adminId = 99L;
        UpdateAdminRequest request = mock(UpdateAdminRequest.class);

        when(adminRepository.findById(adminId)).thenReturn(Optional.empty());

        assertThrows(AdminNotFoundException.class, () -> useCase.execute(adminId, request));
    }
}