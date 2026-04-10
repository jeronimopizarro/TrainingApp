package com.trainingapp.trainingapp.application.useCase.user.trainer;

import com.trainingapp.trainingapp.application.mapper.trainer.TrainerDTOMapper;
import com.trainingapp.trainingapp.application.validator.UserAccessValidator;
import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.domain.exception.user.TrainerNotFoundException;
import com.trainingapp.trainingapp.domain.repository.user.TrainerRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.user.trainer.TrainerResponse;
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
class GetTrainerByIdUseCaseTest {

    @Mock private TrainerRepository trainerRepository;
    @Mock private SecurityUtils securityUtils;
    @Mock private TrainerDTOMapper trainerDTOMapper;
    @Mock private UserAccessValidator userAccessValidator;

    @InjectMocks private GetTrainerByIdUseCase useCase;

    @Test
    @DisplayName("Debería retornar un entrenador por ID")
    void shouldReturnTrainer() {
        Long id = 1L;
        Trainer mockTrainer = mock(Trainer.class);

        when(trainerRepository.findById(id)).thenReturn(Optional.of(mockTrainer));
        when(mockTrainer.getGymId()).thenReturn(10L);

        doNothing().when(securityUtils).validateSameGym(10L);
        doNothing().when(userAccessValidator).validateReadPermission(mockTrainer);

        when(trainerDTOMapper.toResponse(mockTrainer)).thenReturn(mock(TrainerResponse.class));

        assertNotNull(useCase.execute(id));
        verify(securityUtils).validateSameGym(10L);
        verify(userAccessValidator).validateReadPermission(mockTrainer);
    }

    @Test
    @DisplayName("Debería lanzar error al buscar un ID inexistente")
    void shouldThrowExceptionWhenNotFound() {
        when(trainerRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(TrainerNotFoundException.class, () -> useCase.execute(99L));
    }
}