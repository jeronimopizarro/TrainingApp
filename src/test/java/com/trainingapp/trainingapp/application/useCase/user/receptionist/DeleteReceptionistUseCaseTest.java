package com.trainingapp.trainingapp.application.useCase.user.receptionist;

import com.trainingapp.trainingapp.application.validator.UserAccessValidator;
import com.trainingapp.trainingapp.domain.entity.user.Receptionist;
import com.trainingapp.trainingapp.domain.repository.user.ReceptionistRepository;
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
class DeleteReceptionistUseCaseTest {

    @Mock private ReceptionistRepository receptionistRepository;
    @Mock private SecurityUtils securityUtils;
    @Mock private UserAccessValidator userAccessValidator;

    @InjectMocks private DeleteReceptionistUseCase useCase;

    @Test
    @DisplayName("Debería desactivar un recepcionista exitosamente")
    void shouldDeactivateReceptionist() {
        Long id = 1L;
        Receptionist mockReceptionist = mock(Receptionist.class);

        when(receptionistRepository.findById(id)).thenReturn(Optional.of(mockReceptionist));
        when(mockReceptionist.getGymId()).thenReturn(10L);
        when(mockReceptionist.getId()).thenReturn(id);

        doNothing().when(securityUtils).validateSameGym(10L);
        doNothing().when(userAccessValidator).validateWritePermission(id);

        useCase.execute(id);

        verify(mockReceptionist).deactivate();
        verify(receptionistRepository).save(mockReceptionist);
    }
}