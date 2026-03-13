package com.trainingapp.trainingapp.domain.entity.user;

import com.trainingapp.trainingapp.domain.enums.user.Role;
import lombok.Getter;

@Getter
public class Admin extends User {

    private Long gymId;

    public Admin(String firstName, String lastName, String email, String password, Role role, Long gymId) {
        super(firstName, lastName, email, password, role);

        if (role == Role.SUPER_ADMIN && gymId != null) {
            throw new IllegalArgumentException("A SUPER_ADMIN cannot be tied to a specific gym.");
        }
        if (role == Role.GYM_ADMIN && (gymId == null || gymId <= 0)) {
            throw new IllegalArgumentException("A GYM_ADMIN must be associated with a valid Gym ID.");
        }
        if (role == Role.TRAINER || role == Role.MEMBER) {
            throw new IllegalArgumentException("Invalid role for an Admin entity.");
        }

        this.gymId = gymId;
    }

    public void updateProfile(String firstName, String lastName) {
        super.updateBasicProfile(firstName, lastName);
    }
}