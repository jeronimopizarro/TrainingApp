package com.trainingapp.trainingapp.application.useCase.user.receptionist;

import com.trainingapp.trainingapp.application.useCase.user.ReceptionistNotFoundException;
import com.trainingapp.trainingapp.application.validator.UserAccessValidator;
import com.trainingapp.trainingapp.domain.entity.user.Receptionist;
import com.trainingapp.trainingapp.domain.repository.user.ReceptionistRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteReceptionistUseCase {

    private final ReceptionistRepository receptionistRepository;
    private final SecurityUtils securityUtils;
    private final UserAccessValidator userAccessValidator;

    public DeleteReceptionistUseCase(ReceptionistRepository receptionistRepository,
                                     SecurityUtils securityUtils,
                                     UserAccessValidator userAccessValidator) {
        this.receptionistRepository = receptionistRepository;
        this.securityUtils = securityUtils;
        this.userAccessValidator = userAccessValidator;
    }

    @Transactional
    public void execute(Long id) {
        Receptionist receptionist = findReceptionistOrThrow(id);

        securityUtils.validateSameGym(receptionist.getGymId());
        userAccessValidator.validateWritePermission(receptionist.getId());

        receptionist.deactivate();
        receptionistRepository.save(receptionist);
    }

    private Receptionist findReceptionistOrThrow(Long id) {
        return receptionistRepository.findById(id)
                .orElseThrow(() -> new ReceptionistNotFoundException(id));
    }
}