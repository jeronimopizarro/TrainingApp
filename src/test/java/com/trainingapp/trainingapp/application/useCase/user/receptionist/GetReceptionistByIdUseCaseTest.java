package com.trainingapp.trainingapp.application.useCase.user.receptionist;

import com.trainingapp.trainingapp.application.mapper.receptionist.ReceptionistDTOMapper;
import com.trainingapp.trainingapp.application.validator.UserAccessValidator;
import com.trainingapp.trainingapp.application.useCase.user.ReceptionistNotFoundException;
import com.trainingapp.trainingapp.domain.entity.user.Receptionist;
import com.trainingapp.trainingapp.domain.repository.user.ReceptionistRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.user.receptionist.ReceptionistResponse;
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
class GetReceptionistByIdUseCaseTest {

    @Mock private ReceptionistRepository receptionistRepository;
    @Mock private SecurityUtils securityUtils;
    @Mock private ReceptionistDTOMapper receptionistDTOMapper;
    @Mock private UserAccessValidator userAccessValidator;

    @InjectMocks private GetReceptionistByIdUseCase useCase;

    @Test
    @DisplayName("Debería retornar un recepcionista por ID")
    void shouldReturnReceptionist() {
        Long id = 1L;
        Receptionist mockReceptionist = mock(Receptionist.class);

        when(receptionistRepository.findById(id)).thenReturn(Optional.of(mockReceptionist));
        when(mockReceptionist.getGymId()).thenReturn(10L);

        doNothing().when(securityUtils).validateSameGym(10L);
        doNothing().when(userAccessValidator).validateReadPermission(mockReceptionist);

        when(receptionistDTOMapper.toResponse(mockReceptionist)).thenReturn(mock(ReceptionistResponse.class));

        assertNotNull(useCase.execute(id));
        verify(securityUtils).validateSameGym(10L);
        verify(userAccessValidator).validateReadPermission(mockReceptionist);
    }
}