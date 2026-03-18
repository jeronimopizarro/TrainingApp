package com.trainingapp.trainingapp.application.validator;

import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class UserAccessValidator {

    private final SecurityUtils securityUtils;

    public UserAccessValidator(SecurityUtils securityUtils) {
        this.securityUtils = securityUtils;
    }

    public void validateWritePermission(Long targetUserId){
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.getRole() == Role.SUPER_ADMIN || currentUser.getRole() == Role.GYM_ADMIN) {
            return;
        }

        if (!currentUser.getId().equals(targetUserId)) {
            throw new AccessDeniedException("Acceso denegado: Solo tienes permiso para modificar tu propio perfil.");
        }
    }
}