package com.trainingapp.trainingapp.application.useCase.user.receptionist;

import com.trainingapp.trainingapp.application.mapper.receptionist.ReceptionistDTOMapper;
import com.trainingapp.trainingapp.application.validator.GymValidator;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllReceptionistsByGymIdUseCaseTest {

    @Mock private ReceptionistRepository receptionistRepository;
    @Mock private SecurityUtils securityUtils;
    @Mock private ReceptionistDTOMapper receptionistDTOMapper;
    @Mock private GymValidator gymValidator;

    @InjectMocks private GetAllReceptionistsByGymIdUseCase useCase;

    @Test
    @DisplayName("Debería retornar una lista de recepcionistas por GymId")
    void shouldReturnList() {
        Long gymId = 10L;
        Receptionist mockReceptionist = mock(Receptionist.class);

        doNothing().when(gymValidator).validateExists(gymId);
        doNothing().when(securityUtils).validateSameGym(gymId);

        when(receptionistRepository.findAllByGymId(gymId)).thenReturn(List.of(mockReceptionist));
        when(receptionistDTOMapper.toResponse(mockReceptionist)).thenReturn(mock(ReceptionistResponse.class));

        List<ReceptionistResponse> response = useCase.execute(gymId);

        assertEquals(1, response.size());
        verify(gymValidator).validateExists(gymId);
        verify(securityUtils).validateSameGym(gymId);
    }
}