package com.trainingapp.trainingapp.application.useCase.user.receptionist;

import com.trainingapp.trainingapp.application.mapper.receptionist.ReceptionistDTOMapper;
import com.trainingapp.trainingapp.application.validator.GymValidator;
import com.trainingapp.trainingapp.application.validator.UserRegistrationValidator;
import com.trainingapp.trainingapp.domain.entity.user.Receptionist;
import com.trainingapp.trainingapp.domain.repository.user.ReceptionistRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.user.receptionist.ReceptionistResponse;
import com.trainingapp.trainingapp.web.dto.user.receptionist.RegisterReceptionistRequest;
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
class RegisterReceptionistUseCaseTest {

    @Mock private ReceptionistRepository receptionistRepository;
    @Mock private UserRegistrationValidator userRegistrationValidator;
    @Mock private GymValidator gymValidator;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private ReceptionistDTOMapper mapper;
    @Mock private SecurityUtils securityUtils;

    @InjectMocks private RegisterReceptionistUseCase useCase;

    @Test
    @DisplayName("Debería registrar un nuevo recepcionista exitosamente")
    void shouldRegisterReceptionistSuccessfully() {
        Long gymId = 10L;
        RegisterReceptionistRequest request = new RegisterReceptionistRequest(
                "Recep", "Test", "recep@test.com", "pass123", "12345678", gymId
        );

        doNothing().when(securityUtils).validateSameGym(gymId);
        doNothing().when(userRegistrationValidator).validateEmailIsUnique("recep@test.com");
        doNothing().when(gymValidator).validateExists(gymId);

        when(passwordEncoder.encode("pass123")).thenReturn("encoded");

        Receptionist mockReceptionist = mock(Receptionist.class);
        when(receptionistRepository.save(any(Receptionist.class))).thenReturn(mockReceptionist);
        when(mapper.toResponse(mockReceptionist)).thenReturn(mock(ReceptionistResponse.class));

        ReceptionistResponse response = useCase.execute(request);

        assertNotNull(response);
        verify(securityUtils).validateSameGym(gymId);
        verify(userRegistrationValidator).validateEmailIsUnique("recep@test.com");
        verify(gymValidator).validateExists(gymId);
        verify(receptionistRepository).save(any(Receptionist.class));
    }
}