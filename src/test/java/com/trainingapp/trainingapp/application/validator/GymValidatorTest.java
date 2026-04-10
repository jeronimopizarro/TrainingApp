package com.trainingapp.trainingapp.application.validator;

import com.trainingapp.trainingapp.domain.exception.gym.GymNotFoundException;
import com.trainingapp.trainingapp.domain.repository.gym.GymRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GymValidatorTest {

    @Mock
    private GymRepository gymRepository;

    @InjectMocks
    private GymValidator gymValidator;

    @Test
    @DisplayName("Debería pasar si el gimnasio existe")
    void shouldPassIfGymExists() {
        when(gymRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> gymValidator.validateExists(1L));
    }

    @Test
    @DisplayName("Debería lanzar error si el gimnasio no existe")
    void shouldThrowIfGymDoesNotExist() {
        when(gymRepository.existsById(1L)).thenReturn(false);
        assertThrows(GymNotFoundException.class, () -> gymValidator.validateExists(1L));
    }
}