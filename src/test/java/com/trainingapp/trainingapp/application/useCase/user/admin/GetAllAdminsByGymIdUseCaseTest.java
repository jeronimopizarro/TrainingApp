package com.trainingapp.trainingapp.application.useCase.user.admin;

import com.trainingapp.trainingapp.application.mapper.admin.AdminDTOMapper;
import com.trainingapp.trainingapp.application.validator.GymValidator;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllAdminsByGymIdUseCaseTest {

    @Mock private AdminRepository adminRepository;
    @Mock private SecurityUtils securityUtils;
    @Mock private AdminDTOMapper adminDTOMapper;
    @Mock private GymValidator gymValidator;

    @InjectMocks private GetAllAdminsByGymIdUseCase useCase;

    @Test
    @DisplayName("Debería retornar una lista de administradores por GymId")
    void shouldReturnList() {
        Long gymId = 10L;
        Admin mockAdmin = mock(Admin.class);

        doNothing().when(gymValidator).validateExists(gymId);
        doNothing().when(securityUtils).validateSameGym(gymId);

        when(adminRepository.findByGymId(gymId)).thenReturn(List.of(mockAdmin));
        when(adminDTOMapper.toResponse(mockAdmin)).thenReturn(mock(AdminResponse.class));

        List<AdminResponse> response = useCase.execute(gymId);

        assertEquals(1, response.size());
        verify(gymValidator).validateExists(gymId);
        verify(securityUtils).validateSameGym(gymId);
    }
}