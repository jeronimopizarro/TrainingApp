package com.trainingapp.trainingapp.application.useCase.user.admin;

import com.trainingapp.trainingapp.application.mapper.admin.AdminDTOMapper;
import com.trainingapp.trainingapp.application.validator.GymValidator;
import com.trainingapp.trainingapp.application.validator.UserRegistrationValidator;
import com.trainingapp.trainingapp.domain.entity.user.Admin;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.domain.repository.user.AdminRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.user.admin.AdminResponse;
import com.trainingapp.trainingapp.web.dto.user.admin.RegisterAdminRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterAdminUseCaseTest {

    @Mock private AdminRepository adminRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AdminDTOMapper adminDTOMapper;
    @Mock private UserRegistrationValidator registrationValidator;
    @Mock private GymValidator gymValidator;
    @Mock private SecurityUtils securityUtils;

    @InjectMocks private RegisterAdminUseCase useCase;

    @Test
    @DisplayName("Debería registrar un GYM_ADMIN validando el gimnasio")
    void shouldRegisterGymAdminSuccessfully() {
        Long gymId = 10L;
        RegisterAdminRequest request = mock(RegisterAdminRequest.class);
        when(request.role()).thenReturn(Role.GYM_ADMIN);
        when(request.gymId()).thenReturn(gymId);
        when(request.email()).thenReturn("gym@test.com");
        when(request.password()).thenReturn("pass123");

        doNothing().when(gymValidator).validateExists(gymId);
        doNothing().when(securityUtils).validateSameGym(gymId);
        doNothing().when(registrationValidator).validateEmailIsUnique("gym@test.com");

        when(passwordEncoder.encode("pass123")).thenReturn("encoded");

        Admin mockAdmin = mock(Admin.class);
        when(adminDTOMapper.toDomain(request, Role.GYM_ADMIN, "encoded")).thenReturn(mockAdmin);
        when(adminRepository.save(mockAdmin)).thenReturn(mockAdmin);
        when(adminDTOMapper.toResponse(mockAdmin)).thenReturn(mock(AdminResponse.class));

        AdminResponse response = useCase.execute(request);

        assertNotNull(response);
        verify(gymValidator).validateExists(gymId);
    }

    @Test
    @DisplayName("Debería registrar un SUPER_ADMIN sin validar el gimnasio")
    void shouldRegisterSuperAdminSuccessfully() {
        RegisterAdminRequest request = mock(RegisterAdminRequest.class);
        when(request.role()).thenReturn(Role.SUPER_ADMIN);
        when(request.email()).thenReturn("super@test.com");
        when(request.password()).thenReturn("pass123");

        // No se debe llamar a gymValidator ni securityUtils
        doNothing().when(registrationValidator).validateEmailIsUnique("super@test.com");
        when(passwordEncoder.encode("pass123")).thenReturn("encoded");

        Admin mockAdmin = mock(Admin.class);
        when(adminDTOMapper.toDomain(request, Role.SUPER_ADMIN, "encoded")).thenReturn(mockAdmin);
        when(adminRepository.save(mockAdmin)).thenReturn(mockAdmin);
        when(adminDTOMapper.toResponse(mockAdmin)).thenReturn(mock(AdminResponse.class));

        AdminResponse response = useCase.execute(request);

        assertNotNull(response);
        verify(gymValidator, never()).validateExists(any());
        verify(securityUtils, never()).validateSameGym(any());
    }
}