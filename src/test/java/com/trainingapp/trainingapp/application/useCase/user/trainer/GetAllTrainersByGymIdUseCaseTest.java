package com.trainingapp.trainingapp.application.useCase.user.trainer;

import com.trainingapp.trainingapp.application.mapper.trainer.TrainerDTOMapper;
import com.trainingapp.trainingapp.application.validator.GymValidator;
import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.domain.repository.user.TrainerRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.user.trainer.TrainerResponse;
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
class GetAllTrainersByGymIdUseCaseTest {

    @Mock private TrainerRepository trainerRepository;
    @Mock private SecurityUtils securityUtils;
    @Mock private GymValidator gymValidator;
    @Mock private TrainerDTOMapper trainerDTOMapper;

    @InjectMocks private GetAllTrainersByGymIdUseCase useCase;

    @Test
    @DisplayName("Debería retornar lista de entrenadores por GymId")
    void shouldReturnList() {
        Long gymId = 10L;
        Trainer mockTrainer = mock(Trainer.class);

        doNothing().when(gymValidator).validateExists(gymId);
        doNothing().when(securityUtils).validateSameGym(gymId);

        when(trainerRepository.findByGymId(gymId)).thenReturn(List.of(mockTrainer));
        when(trainerDTOMapper.toResponse(mockTrainer)).thenReturn(mock(TrainerResponse.class));

        List<TrainerResponse> response = useCase.execute(gymId);

        assertEquals(1, response.size());
        verify(gymValidator).validateExists(gymId);
        verify(securityUtils).validateSameGym(gymId);
    }
}