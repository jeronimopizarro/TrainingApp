package com.trainingapp.trainingapp.application.useCase.user.trainer;

import com.trainingapp.trainingapp.application.validator.UserAccessValidator;
import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.domain.repository.user.TrainerRepository;
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
class DeleteTrainerUseCaseTest {

    @Mock private TrainerRepository trainerRepository;
    @Mock private SecurityUtils securityUtils;
    @Mock private UserAccessValidator userAccessValidator;

    @InjectMocks private DeleteTrainerUseCase useCase;

    @Test
    @DisplayName("Debería desactivar un entrenador exitosamente")
    void shouldDeactivateTrainer() {
        Long id = 1L;
        Trainer mockTrainer = mock(Trainer.class);

        when(trainerRepository.findById(id)).thenReturn(Optional.of(mockTrainer));
        when(mockTrainer.getGymId()).thenReturn(10L);
        when(mockTrainer.getId()).thenReturn(id);

        doNothing().when(securityUtils).validateSameGym(10L);
        doNothing().when(userAccessValidator).validateWritePermission(id);

        useCase.execute(id);

        verify(mockTrainer).deactivate();
        verify(trainerRepository).save(mockTrainer);
    }
}