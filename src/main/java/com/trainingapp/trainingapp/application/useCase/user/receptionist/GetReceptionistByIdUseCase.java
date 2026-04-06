package com.trainingapp.trainingapp.application.useCase.user.receptionist;

import com.trainingapp.trainingapp.application.mapper.receptionist.ReceptionistDTOMapper;
import com.trainingapp.trainingapp.application.useCase.user.ReceptionistNotFoundException;
import com.trainingapp.trainingapp.application.validator.UserAccessValidator;
import com.trainingapp.trainingapp.domain.entity.user.Receptionist;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.domain.exception.user.UnauthorizedProfileAccessException;
import com.trainingapp.trainingapp.domain.repository.user.ReceptionistRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.user.receptionist.ReceptionistResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetReceptionistByIdUseCase {

    private final ReceptionistRepository receptionistRepository;
    private final SecurityUtils securityUtils;
    private final ReceptionistDTOMapper receptionistDTOMapper;
    private final UserAccessValidator userAccessValidator;

    public GetReceptionistByIdUseCase(ReceptionistRepository receptionistRepository,
                                      SecurityUtils securityUtils,
                                      ReceptionistDTOMapper receptionistDTOMapper,
                                      UserAccessValidator userAccessValidator) {
        this.receptionistRepository = receptionistRepository;
        this.securityUtils = securityUtils;
        this.receptionistDTOMapper = receptionistDTOMapper;
        this.userAccessValidator = userAccessValidator;
    }

    @Transactional(readOnly = true)
    public ReceptionistResponse execute(Long id) {
        Receptionist receptionist = findReceptionistOrThrow(id);

        securityUtils.validateSameGym(receptionist.getGymId());
        userAccessValidator.validateReadPermission(receptionist);

        return receptionistDTOMapper.toResponse(receptionist);
    }

    private Receptionist findReceptionistOrThrow(Long id) {
        return receptionistRepository.findById(id)
                .orElseThrow(() -> new ReceptionistNotFoundException(id));
    }
}