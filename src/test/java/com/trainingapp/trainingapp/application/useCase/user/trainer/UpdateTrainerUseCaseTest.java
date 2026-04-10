package com.trainingapp.trainingapp.application.useCase.user.trainer;

import com.trainingapp.trainingapp.application.mapper.trainer.TrainerDTOMapper;
import com.trainingapp.trainingapp.application.validator.UserAccessValidator;
import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.domain.exception.user.TrainerNotFoundException;
import com.trainingapp.trainingapp.domain.repository.user.TrainerRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.user.trainer.TrainerResponse;
import com.trainingapp.trainingapp.web.dto.user.trainer.UpdateTrainerRequest;
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
class UpdateTrainerUseCaseTest {

    @Mock private TrainerRepository trainerRepository;
    @Mock private SecurityUtils securityUtils;
    @Mock private TrainerDTOMapper trainerDTOMapper;
    @Mock private UserAccessValidator userAccessValidator;

    @InjectMocks private UpdateTrainerUseCase useCase;

    @Test
    @DisplayName("Debería actualizar un entrenador exitosamente")
    void shouldUpdateTrainerSuccessfully() {
        Long id = 1L;
        Long gymId = 10L;
        UpdateTrainerRequest request = mock(UpdateTrainerRequest.class);
        when(request.firstName()).thenReturn("Nuevo");
        when(request.lastName()).thenReturn("Nombre");
        when(request.dni()).thenReturn("12345678");
        when(request.specialization()).thenReturn("Musculación");

        Trainer mockTrainer = mock(Trainer.class);
        when(trainerRepository.findById(id)).thenReturn(Optional.of(mockTrainer));
        when(mockTrainer.getGymId()).thenReturn(gymId);
        when(mockTrainer.getId()).thenReturn(id);

        doNothing().when(securityUtils).validateSameGym(gymId);
        doNothing().when(userAccessValidator).validateWritePermission(id);

        when(trainerRepository.save(mockTrainer)).thenReturn(mockTrainer);
        when(trainerDTOMapper.toResponse(mockTrainer)).thenReturn(mock(TrainerResponse.class));

        TrainerResponse response = useCase.execute(id, request);

        assertNotNull(response);
        verify(mockTrainer).updateTrainerDetails("Nuevo", "Nombre", "12345678", "Musculación");
    }

    @Test
    @DisplayName("Debería lanzar error si el entrenador no existe")
    void shouldThrowExceptionWhenNotFound() {
        when(trainerRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(TrainerNotFoundException.class, () -> useCase.execute(99L, mock(UpdateTrainerRequest.class)));
    }
}