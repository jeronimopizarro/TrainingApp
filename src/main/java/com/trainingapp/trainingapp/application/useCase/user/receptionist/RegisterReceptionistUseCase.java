package com.trainingapp.trainingapp.application.useCase.user.receptionist;

import com.trainingapp.trainingapp.application.mapper.receptionist.ReceptionistDTOMapper;
import com.trainingapp.trainingapp.application.validator.GymValidator;
import com.trainingapp.trainingapp.application.validator.UserRegistrationValidator;
import com.trainingapp.trainingapp.domain.entity.user.Receptionist;
import com.trainingapp.trainingapp.domain.repository.user.ReceptionistRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.user.receptionist.ReceptionistResponse;
import com.trainingapp.trainingapp.web.dto.user.receptionist.RegisterReceptionistRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterReceptionistUseCase {
    private final ReceptionistRepository receptionistRepository;
    private final UserRegistrationValidator userRegistrationValidator;
    private final GymValidator gymValidator;
    private final PasswordEncoder passwordEncoder;
    private final ReceptionistDTOMapper mapper;
    private final SecurityUtils securityUtils;

    public RegisterReceptionistUseCase(ReceptionistRepository receptionistRepository,
                                       UserRegistrationValidator userRegistrationValidator,
                                       GymValidator gymValidator, PasswordEncoder passwordEncoder,
                                       ReceptionistDTOMapper mapper, SecurityUtils securityUtils) {
        this.receptionistRepository = receptionistRepository;
        this.userRegistrationValidator = userRegistrationValidator;
        this.gymValidator = gymValidator;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public ReceptionistResponse execute(RegisterReceptionistRequest request) {
        // 1. Validar permisos (Solo un Admin de ese gimnasio puede crear un recepcionista)
        securityUtils.validateSameGym(request.gymId());

        userRegistrationValidator.validateEmailIsUnique(request.email());
        gymValidator.validateExists(request.gymId());

        String encodedPassword = passwordEncoder.encode(request.password());

        Receptionist receptionist = createReceptionist(request, encodedPassword);

        Receptionist savedReceptionist = receptionistRepository.save(receptionist);
        return mapper.toResponse(savedReceptionist);
    }

    private Receptionist createReceptionist(RegisterReceptionistRequest request,
                                                 String encodedPassword) {
        return Receptionist.createNew(
                request.firstName(), request.lastName(), request.email(),
                encodedPassword, request.dni(), request.gymId()
        );
    }
}
