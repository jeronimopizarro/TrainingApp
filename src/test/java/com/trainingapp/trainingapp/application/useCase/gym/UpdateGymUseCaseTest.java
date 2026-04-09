package com.trainingapp.trainingapp.application.useCase.gym;

import com.trainingapp.trainingapp.application.mapper.gym.GymDTOMapper;
import com.trainingapp.trainingapp.domain.entity.gym.Gym;
import com.trainingapp.trainingapp.domain.exception.gym.DuplicateGymNameException;
import com.trainingapp.trainingapp.domain.repository.gym.GymRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.gym.GymResponse;
import com.trainingapp.trainingapp.web.dto.gym.UpdateGymRequest;
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
class UpdateGymUseCaseTest {

    @Mock private GymRepository gymRepository;
    @Mock private SecurityUtils securityUtils;
    @Mock private GymDTOMapper gymDTOMapper;

    @InjectMocks private UpdateGymUseCase useCase;

    @Test
    @DisplayName("Debería actualizar los detalles del gimnasio exitosamente")
    void shouldUpdateGymSuccessfully() {
        UpdateGymRequest request = new UpdateGymRequest("New Gym Name", "New Address", "987654321");
        Gym mockGym = mock(Gym.class);

        when(gymRepository.findById(1L)).thenReturn(Optional.of(mockGym));
        when(mockGym.getId()).thenReturn(1L);
        doNothing().when(securityUtils).validateSameGym(1L);

        when(gymRepository.existsByNameAndIdNot("New Gym Name", 1L)).thenReturn(false);
        when(gymRepository.save(mockGym)).thenReturn(mockGym);
        when(gymDTOMapper.toResponse(mockGym)).thenReturn(mock(GymResponse.class));

        GymResponse response = useCase.execute(1L, request);

        assertNotNull(response);
        verify(mockGym).updateDetails("New Gym Name", "New Address", "987654321");
        verify(gymRepository).save(mockGym);
    }

    @Test
    @DisplayName("Debería lanzar DuplicateGymNameException si el nuevo nombre ya pertenece a otro gym")
    void shouldThrowExceptionWhenDuplicateName() {
        UpdateGymRequest request = new UpdateGymRequest("Existing Name", "New Address", "987654321");
        Gym mockGym = mock(Gym.class);

        when(gymRepository.findById(1L)).thenReturn(Optional.of(mockGym));
        when(mockGym.getId()).thenReturn(1L);
        doNothing().when(securityUtils).validateSameGym(1L);

        when(gymRepository.existsByNameAndIdNot("Existing Name", 1L)).thenReturn(true);

        assertThrows(DuplicateGymNameException.class, () -> useCase.execute(1L, request));
        verify(mockGym, never()).updateDetails(anyString(), anyString(), anyString());
    }
}