package com.trainingapp.trainingapp.application.validator;

import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.entity.user.Admin;
import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.domain.exception.routine.UnauthorizedRoutineAccessException;
import com.trainingapp.trainingapp.domain.exception.routine.UnauthorizedRoutineModificationException;
import com.trainingapp.trainingapp.domain.exception.user.TrainerNotFoundException;
import com.trainingapp.trainingapp.domain.repository.user.UserRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class RoutineAccessValidator {

    private final SecurityUtils securityUtils;
    private final UserRepository userRepository;

    public RoutineAccessValidator(SecurityUtils securityUtils, UserRepository userRepository) {
        this.securityUtils = securityUtils;
        this.userRepository = userRepository;
    }

    public void validateModificationPermission(Routine routine) {
        User currentUser = securityUtils.getCurrentUser();
        if (currentUser.isSuperAdmin()) return;

        securityUtils.validateSameGym(routine.getGymId());

        if (currentUser.isMember()) {
            if (!routine.getMemberId().equals(currentUser.getId())) {
                throw new UnauthorizedRoutineModificationException();
            }
        } else if (currentUser.isTrainer()) {
            boolean isCreator = routine.getCreatedByUserId().equals(currentUser.getId());
            boolean isAssigned = routine.getTrainerId() != null && routine.getTrainerId().equals(currentUser.getId());

            if (!isCreator && !isAssigned) {
                throw new UnauthorizedRoutineModificationException();
            }
        }
    }

    public void validateReadPermission(Routine routine) {
        User currentUser = securityUtils.getCurrentUser();
        if (currentUser.isSuperAdmin()) return;

        securityUtils.validateSameGym(routine.getGymId());

        if (currentUser.isMember()) {
            if (!routine.getMemberId().equals(currentUser.getId())) {
                throw new UnauthorizedRoutineAccessException();
            }
        }
    }

    public void validateTargetMemberAccess(Long targetMemberId) {
        User currentUser = securityUtils.getCurrentUser();
        if (currentUser.isSuperAdmin()) return;

        if (currentUser.isMember()) {
            if (!currentUser.getId().equals(targetMemberId)) {
                throw new UnauthorizedRoutineAccessException();
            }
            return;
        }

        User targetUser = userRepository.findById(targetMemberId)
                .orElseThrow(() -> new TrainerNotFoundException(targetMemberId));
        securityUtils.validateSameGym(extractTargetGymId(targetUser));
    }

    public void validateTargetTrainerAccess(Long targetTrainerId) {
        User currentUser = securityUtils.getCurrentUser();
        if (currentUser.isSuperAdmin()) return;

        if (currentUser.isTrainer()) {
            if (!currentUser.getId().equals(targetTrainerId)) {
                throw new UnauthorizedRoutineAccessException();
            }
            return;
        }

        User targetUser = userRepository.findById(targetTrainerId)
                .orElseThrow(() -> new TrainerNotFoundException(targetTrainerId));
        securityUtils.validateSameGym(extractTargetGymId(targetUser));
    }

    // Método utilitario interno
    private Long extractTargetGymId(User targetUser) {
        if (targetUser instanceof Member member) return member.getGymId();
        if (targetUser instanceof Trainer trainer) return trainer.getGymId();
        if (targetUser instanceof Admin admin) return admin.getGymId();
        return null;
    }
}
