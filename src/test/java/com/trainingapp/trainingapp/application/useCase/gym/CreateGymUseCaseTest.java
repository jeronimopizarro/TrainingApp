package com.trainingapp.trainingapp.application.useCase.gym;

import com.trainingapp.trainingapp.application.mapper.gym.GymDTOMapper;
import com.trainingapp.trainingapp.domain.entity.gym.Gym;
import com.trainingapp.trainingapp.domain.exception.gym.GymAlreadyExistsException;
import com.trainingapp.trainingapp.domain.repository.gym.GymRepository;
import com.trainingapp.trainingapp.web.dto.gym.CreateGymRequest;
import com.trainingapp.trainingapp.web.dto.gym.GymResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateGymUseCaseTest {

    @Mock private GymRepository gymRepository;
    @Mock private GymDTOMapper gymDTOMapper;

    @InjectMocks private CreateGymUseCase useCase;

    @Test
    @DisplayName("Debería crear un gimnasio exitosamente")
    void shouldCreateGymSuccessfully() {
        CreateGymRequest request = new CreateGymRequest("Gym Center", "Calle Falsa 123", "123456789");
        Gym mockGym = mock(Gym.class);
        Gym savedGym = mock(Gym.class);
        GymResponse mockResponse = mock(GymResponse.class);

        when(gymRepository.existsByName("Gym Center")).thenReturn(false);
        when(gymDTOMapper.toDomain(request)).thenReturn(mockGym);
        when(gymRepository.save(mockGym)).thenReturn(savedGym);
        when(gymDTOMapper.toResponse(savedGym)).thenReturn(mockResponse);

        GymResponse response = useCase.execute(request);

        assertNotNull(response);
        verify(gymRepository).save(mockGym);
    }

    @Test
    @DisplayName("Debería lanzar GymAlreadyExistsException si el nombre ya está en uso")
    void shouldThrowExceptionWhenNameAlreadyExists() {
        CreateGymRequest request = new CreateGymRequest("Gym Center", "Calle Falsa 123", "123456789");

        when(gymRepository.existsByName("Gym Center")).thenReturn(true);

        assertThrows(GymAlreadyExistsException.class, () -> useCase.execute(request));
        verify(gymRepository, never()).save(any());
    }
}