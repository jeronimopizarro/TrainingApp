package com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security;

import com.trainingapp.trainingapp.domain.entity.user.Admin;
import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.domain.repository.user.UserRepository;
import org.springframework.security.access.AccessDeniedException;
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
                .orElseThrow(() -> new AccessDeniedException("Usuario no encontrado o no autenticado."));
    }

    // ¿El usuario actual pertenece a este gimnasio?
    public void validateSameGym(Long targetGymId) {
        User currentUser = getCurrentUser();
        if (currentUser.getRole() == Role.SUPER_ADMIN) return;

        Long currentUserGymId = getCurrentUserGymId();
        if (!targetGymId.equals(currentUserGymId)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Acceso denegado: El recurso pertenece a otro gimnasio.");
        }
    }

    // Extrae el Gym ID del usuario autenticado
    public Long getCurrentUserGymId() {
        User user = getCurrentUser();
        if (user instanceof Admin admin) return admin.getGymId();
        if (user instanceof Trainer trainer) return trainer.getGymId();
        if (user instanceof Member member) return member.getGymId();
        return null;
    }
}