package com.trainingapp.trainingapp.domain.entity.user;

import com.trainingapp.trainingapp.domain.enums.user.Role;
import lombok.Getter;

@Getter
public class Admin extends User {

    private final Long gymId;

    private Admin(Long id, String firstName, String lastName, String email, String password,
                  String dni, Role role, boolean active, Long gymId) {
        super(id, firstName, lastName, email, password, dni, role, active);
        this.gymId = gymId;
    }

    public static Admin createNew(String firstName, String lastName, String email, String password,
                                  String dni, Role role, Long gymId) {
        return new Admin(null, firstName, lastName, email, password, dni, role, true, gymId);
    }

    public static Admin restore(Long id, String firstName, String lastName, String email,
                                String password, String dni, Role role, boolean active,
                                Long gymId) {
        return new Admin(id, firstName, lastName, email, password, dni, role, active, gymId);
    }
}