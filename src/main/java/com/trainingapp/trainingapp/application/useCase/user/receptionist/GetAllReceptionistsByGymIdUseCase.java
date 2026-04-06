package com.trainingapp.trainingapp.application.useCase.user.receptionist;

import com.trainingapp.trainingapp.application.mapper.receptionist.ReceptionistDTOMapper;
import com.trainingapp.trainingapp.application.validator.GymValidator;
import com.trainingapp.trainingapp.domain.entity.user.Receptionist;
import com.trainingapp.trainingapp.domain.repository.user.ReceptionistRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.user.receptionist.ReceptionistResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GetAllReceptionistsByGymIdUseCase {

    private final ReceptionistRepository receptionistRepository;
    private final SecurityUtils securityUtils;
    private final ReceptionistDTOMapper receptionistDTOMapper;
    private final GymValidator gymValidator;

    public GetAllReceptionistsByGymIdUseCase(ReceptionistRepository receptionistRepository,
                                             SecurityUtils securityUtils,
                                             ReceptionistDTOMapper receptionistDTOMapper,
                                             GymValidator gymValidator) {
        this.receptionistRepository = receptionistRepository;
        this.securityUtils = securityUtils;
        this.receptionistDTOMapper = receptionistDTOMapper;
        this.gymValidator = gymValidator;
    }

    @Transactional(readOnly = true)
    public List<ReceptionistResponse> execute(Long gymId) {
        gymValidator.validateExists(gymId);
        securityUtils.validateSameGym(gymId);

        List<Receptionist> receptionists = receptionistRepository.findAllByGymId(gymId);

        return receptionists.stream()
                .map(receptionistDTOMapper::toResponse)
                .toList();
    }
}