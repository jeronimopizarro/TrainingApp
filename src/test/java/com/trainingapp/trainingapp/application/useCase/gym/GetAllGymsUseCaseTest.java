package com.trainingapp.trainingapp.application.useCase.gym;

import com.trainingapp.trainingapp.application.mapper.gym.GymDTOMapper;
import com.trainingapp.trainingapp.domain.entity.gym.Gym;
import com.trainingapp.trainingapp.domain.repository.gym.GymRepository;
import com.trainingapp.trainingapp.web.dto.gym.GymResponse;
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
class GetAllGymsUseCaseTest {

    @Mock private GymRepository gymRepository;
    @Mock private GymDTOMapper gymDTOMapper;

    @InjectMocks private GetAllGymsUseCase useCase;

    @Test
    @DisplayName("Debería retornar una lista de todos los gimnasios")
    void shouldReturnAllGyms() {
        Gym mockGym = mock(Gym.class);

        when(gymRepository.findAll()).thenReturn(List.of(mockGym));
        when(gymDTOMapper.toResponse(mockGym)).thenReturn(mock(GymResponse.class));

        List<GymResponse> response = useCase.execute();

        assertEquals(1, response.size());
    }
}