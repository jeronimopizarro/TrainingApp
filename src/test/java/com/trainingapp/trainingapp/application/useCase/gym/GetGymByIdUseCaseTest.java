package com.trainingapp.trainingapp.application.useCase.gym;

import com.trainingapp.trainingapp.application.mapper.gym.GymDTOMapper;
import com.trainingapp.trainingapp.domain.entity.gym.Gym;
import com.trainingapp.trainingapp.domain.repository.gym.GymRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.gym.GymResponse;
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
class GetGymByIdUseCaseTest {

    @Mock private GymRepository gymRepository;
    @Mock private SecurityUtils securityUtils;
    @Mock private GymDTOMapper gymDTOMapper;

    @InjectMocks private GetGymByIdUseCase useCase;

    @Test
    @DisplayName("Debería retornar un gimnasio por ID si se tiene permiso")
    void shouldReturnGymById() {
        Gym mockGym = mock(Gym.class);

        when(gymRepository.findById(1L)).thenReturn(Optional.of(mockGym));
        when(mockGym.getId()).thenReturn(1L);
        doNothing().when(securityUtils).validateSameGym(1L);

        when(gymDTOMapper.toResponse(mockGym)).thenReturn(mock(GymResponse.class));

        GymResponse response = useCase.execute(1L);

        assertNotNull(response);
    }
}