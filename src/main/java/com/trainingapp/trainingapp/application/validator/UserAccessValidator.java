package com.trainingapp.trainingapp.application.validator;

import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.domain.exception.AccessDeniedException;
import com.trainingapp.trainingapp.domain.exception.user.UnauthorizedProfileAccessException;
import com.trainingapp.trainingapp.domain.exception.user.UnauthorizedProfileModificationException;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import org.springframework.stereotype.Component;

@Component
public class UserAccessValidator {

    private final SecurityUtils securityUtils;

    public UserAccessValidator(SecurityUtils securityUtils) {
        this.securityUtils = securityUtils;
    }

    public void validateWritePermission(Long targetUserId) {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.isSuperAdmin() || currentUser.isGymAdmin()) return;

        if (!currentUser.getId().equals(targetUserId)) {
            throw new UnauthorizedProfileModificationException();
        }
    }

    public void validateReadPermission(User targetUser) {
        User currentUser = securityUtils.getCurrentUser();

        if (currentUser.isSuperAdmin() || currentUser.isGymAdmin()) return;

        if (currentUser.getId().equals(targetUser.getId())) return;

        if (currentUser.isTrainer() && targetUser.getRole() == Role.MEMBER) return;

        throw new UnauthorizedProfileAccessException();
    }
}
