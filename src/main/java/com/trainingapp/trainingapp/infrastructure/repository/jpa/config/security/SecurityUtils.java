package com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security;

import com.trainingapp.trainingapp.domain.entity.user.*;
import com.trainingapp.trainingapp.domain.exception.auth.UnauthenticatedUserException;
import com.trainingapp.trainingapp.domain.exception.gym.UnauthorizedGymAccessException;
import com.trainingapp.trainingapp.domain.repository.user.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    private final UserRepository userRepository;

    public SecurityUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(UnauthenticatedUserException::new);
    }

    // ¿El usuario actual pertenece a este gimnasio?
    public void validateSameGym(Long targetGymId) {
        User currentUser = getCurrentUser();
        if (currentUser.isSuperAdmin()) return;

        Long currentUserGymId = getCurrentUserGymId();
        if (targetGymId == null || !targetGymId.equals(currentUserGymId)) {
            throw new UnauthorizedGymAccessException();
        }
    }

    // Extrae el Gym ID del usuario autenticado
    public Long getCurrentUserGymId() {
        User user = getCurrentUser();
        if (user instanceof Admin admin) return admin.getGymId();
        if (user instanceof Trainer trainer) return trainer.getGymId();
        if (user instanceof Member member) return member.getGymId();
        if (user instanceof Receptionist receptionist) return receptionist.getGymId();
        return null;
    }
}