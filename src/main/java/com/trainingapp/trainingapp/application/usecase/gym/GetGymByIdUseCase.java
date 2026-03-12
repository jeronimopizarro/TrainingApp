package com.trainingapp.trainingapp.application.usecase.gym;

import com.trainingapp.trainingapp.domain.entity.gym.Gym;
import com.trainingapp.trainingapp.domain.entity.user.Admin;
import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.domain.exception.gym.GymNotFoundException;
import com.trainingapp.trainingapp.domain.repository.gym.GymRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.gym.GymResponse;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class GetGymByIdUseCase {

    private final GymRepository gymRepository;
    private final SecurityUtils securityUtils;

    public GetGymByIdUseCase(GymRepository gymRepository, SecurityUtils securityUtils) {
        this.gymRepository = gymRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public GymResponse execute(Long id) {
        User currentUser = securityUtils.getCurrentUser();

        Gym gym = findGymOrThrow(id);

        validateReadPermission(currentUser, gym.getId());

        return mapToResponse(gym);
    }

    private Gym findGymOrThrow(Long id) {
        return gymRepository.findById(id)
                .orElseThrow(() -> new GymNotFoundException(
                        "The gym with id " + id + " was not found."));
    }

    private void validateReadPermission(User user, Long requestedGymId) {
        if (user.getRole() == Role.SUPER_ADMIN) return;

        Long userGymId = extractGymId(user);
        if (!requestedGymId.equals(userGymId)) {
            throw new AccessDeniedException("No tienes permiso para ver la información de otro gimnasio.");
        }
    }

    private Long extractGymId(User user) {
        if (user instanceof Admin admin) return admin.getGymId();
        if (user instanceof Trainer trainer) return trainer.getGymId();
        if (user instanceof Member member) return member.getGymId();
        return null;
    }

    private GymResponse mapToResponse(Gym gym) {
        return new GymResponse(gym.getId(), gym.getName(), gym.getAddress(), gym.getPhone(),
                gym.isActive());
    }
}