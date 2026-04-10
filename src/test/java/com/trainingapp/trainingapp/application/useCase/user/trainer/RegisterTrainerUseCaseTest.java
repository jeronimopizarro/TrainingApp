package com.trainingapp.trainingapp.application.useCase.user.trainer;

import com.trainingapp.trainingapp.application.mapper.trainer.TrainerDTOMapper;
import com.trainingapp.trainingapp.application.validator.UserRegistrationValidator;
import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.domain.repository.user.TrainerRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.user.trainer.RegisterTrainerRequest;
import com.trainingapp.trainingapp.web.dto.user.trainer.TrainerResponse;
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
class RegisterTrainerUseCaseTest {

    @Mock private TrainerRepository trainerRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private SecurityUtils securityUtils;
    @Mock private TrainerDTOMapper trainerDTOMapper;
    @Mock private UserRegistrationValidator registrationValidator;

    @InjectMocks private RegisterTrainerUseCase useCase;

    @Test
    @DisplayName("Debería registrar un nuevo entrenador exitosamente")
    void shouldRegisterTrainerSuccessfully() {
        Long gymId = 10L;
        RegisterTrainerRequest request = mock(RegisterTrainerRequest.class);
        when(request.gymId()).thenReturn(gymId);
        when(request.email()).thenReturn("trainer@test.com");
        when(request.password()).thenReturn("pass123");

        doNothing().when(securityUtils).validateSameGym(gymId);
        doNothing().when(registrationValidator).validateEmailIsUnique("trainer@test.com");

        when(passwordEncoder.encode("pass123")).thenReturn("encoded");

        Trainer mockTrainer = mock(Trainer.class);
        when(trainerDTOMapper.toDomain(request, "encoded")).thenReturn(mockTrainer);
        when(trainerRepository.save(mockTrainer)).thenReturn(mockTrainer);
        when(trainerDTOMapper.toResponse(mockTrainer)).thenReturn(mock(TrainerResponse.class));

        TrainerResponse response = useCase.execute(request);

        assertNotNull(response);
        verify(securityUtils).validateSameGym(gymId);
        verify(registrationValidator).validateEmailIsUnique("trainer@test.com");
        verify(trainerRepository).save(mockTrainer);
    }
}