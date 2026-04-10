package com.trainingapp.trainingapp.application.useCase.user.receptionist;

import com.trainingapp.trainingapp.application.mapper.receptionist.ReceptionistDTOMapper;
import com.trainingapp.trainingapp.application.validator.UserAccessValidator;
import com.trainingapp.trainingapp.application.useCase.user.ReceptionistNotFoundException;
import com.trainingapp.trainingapp.domain.entity.user.Receptionist;
import com.trainingapp.trainingapp.domain.repository.user.ReceptionistRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.user.receptionist.ReceptionistResponse;
import com.trainingapp.trainingapp.web.dto.user.receptionist.UpdateReceptionistRequest;
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
class UpdateReceptionistUseCaseTest {

    @Mock private ReceptionistRepository receptionistRepository;
    @Mock private SecurityUtils securityUtils;
    @Mock private ReceptionistDTOMapper receptionistDTOMapper;
    @Mock private UserAccessValidator userAccessValidator;

    @InjectMocks private UpdateReceptionistUseCase useCase;

    @Test
    @DisplayName("Debería actualizar un recepcionista exitosamente")
    void shouldUpdateReceptionistSuccessfully() {
        Long id = 1L;
        Long gymId = 10L;
        UpdateReceptionistRequest request = new UpdateReceptionistRequest("Nuevo", "Nombre", "12345678");

        Receptionist mockReceptionist = mock(Receptionist.class);
        when(receptionistRepository.findById(id)).thenReturn(Optional.of(mockReceptionist));
        when(mockReceptionist.getGymId()).thenReturn(gymId);
        when(mockReceptionist.getId()).thenReturn(id);

        doNothing().when(securityUtils).validateSameGym(gymId);
        doNothing().when(userAccessValidator).validateWritePermission(id);

        when(receptionistRepository.save(mockReceptionist)).thenReturn(mockReceptionist);
        when(receptionistDTOMapper.toResponse(mockReceptionist)).thenReturn(mock(ReceptionistResponse.class));

        ReceptionistResponse response = useCase.execute(id, request);

        assertNotNull(response);
        verify(mockReceptionist).updateBaseDetails("Nuevo", "Nombre", "12345678");
    }

    @Test
    @DisplayName("Debería lanzar error si el recepcionista no existe")
    void shouldThrowExceptionWhenNotFound() {
        when(receptionistRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ReceptionistNotFoundException.class, () -> useCase.execute(99L, mock(UpdateReceptionistRequest.class)));
    }
}