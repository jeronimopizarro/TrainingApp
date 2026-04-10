package com.trainingapp.trainingapp.application.useCase.user.admin;

import com.trainingapp.trainingapp.application.mapper.admin.AdminDTOMapper;
import com.trainingapp.trainingapp.application.validator.UserAccessValidator;
import com.trainingapp.trainingapp.domain.entity.user.Admin;
import com.trainingapp.trainingapp.domain.repository.user.AdminRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.user.admin.AdminResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAdminByIdUseCaseTest {

    @Mock private AdminRepository adminRepository;
    @Mock private SecurityUtils securityUtils;
    @Mock private AdminDTOMapper adminDTOMapper;
    @Mock private UserAccessValidator userAccessValidator;

    @InjectMocks private GetAdminByIdUseCase useCase;

    @Test
    @DisplayName("Debería retornar un administrador por su ID")
    void shouldReturnAdmin() {
        Long id = 1L;
        Admin mockAdmin = mock(Admin.class);

        when(adminRepository.findById(id)).thenReturn(Optional.of(mockAdmin));
        when(mockAdmin.getGymId()).thenReturn(10L);

        doNothing().when(securityUtils).validateSameGym(10L);
        doNothing().when(userAccessValidator).validateReadPermission(mockAdmin);

        when(adminDTOMapper.toResponse(mockAdmin)).thenReturn(mock(AdminResponse.class));

        AdminResponse response = useCase.execute(id);

        assertNotNull(response);
        verify(securityUtils).validateSameGym(10L);
        verify(userAccessValidator).validateReadPermission(mockAdmin);
    }
}