package com.trainingapp.trainingapp.application.usecase.gym;

import com.trainingapp.trainingapp.domain.entity.gym.Gym;
import com.trainingapp.trainingapp.domain.entity.user.Admin;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.domain.exception.gym.GymNotFoundException;
import com.trainingapp.trainingapp.domain.repository.gym.GymRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.gym.GymResponse;
import com.trainingapp.trainingapp.web.dto.gym.UpdateGymRequest;
import org.jspecify.annotations.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class UpdateGymUseCase {
    private final GymRepository gymRepository;
    private final SecurityUtils securityUtils;

    public UpdateGymUseCase(GymRepository gymRepository, SecurityUtils securityUtils) {
        this.gymRepository = gymRepository;
        this.securityUtils = securityUtils;
    }

    public GymResponse execute(Long id, UpdateGymRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        Gym gym = findGymOrThrow(id);

        validateUpdatePermission(currentUser, gym.getId());

        gym.updateDetails(request.name(), request.address(), request.phone());

        Gym updatedGym = gymRepository.save(gym);

        return mapToResponse(updatedGym);
    }

    private Gym findGymOrThrow(Long id) {
        return gymRepository.findById(id).orElseThrow(
                () -> new GymNotFoundException("The gym with id " + id + " was not found."));
    }

    private void validateUpdatePermission(User user, Long requestedGymId) {
        if (user.getRole() == Role.SUPER_ADMIN) return;

        // Si llegó aca si o si es un GYM_ADMIN.
        Admin admin = (Admin) user;
        if (!requestedGymId.equals(admin.getGymId())) {
            throw new AccessDeniedException("No tienes permiso para modificar los datos de otro gimnasio.");
        }
    }

    private GymResponse mapToResponse(Gym updatedGym) {
        return new GymResponse(updatedGym.getId(), updatedGym.getName(), updatedGym.getAddress(),
                updatedGym.getPhone(), updatedGym.isActive());
    }
}