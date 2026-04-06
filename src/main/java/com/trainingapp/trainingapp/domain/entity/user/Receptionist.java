package com.trainingapp.trainingapp.domain.entity.user;

import com.trainingapp.trainingapp.domain.enums.user.Role;
import lombok.Getter;

@Getter
public class Receptionist extends User {

    private Long gymId;

    private Receptionist(Long id, String firstName, String lastName, String email,
                         String password, String dni, boolean active, Long gymId) {
        super(id, firstName, lastName, email, password, dni, Role.RECEPTIONIST, active);
        this.gymId = gymId;
        validate();
    }

    private void validate() {
        if (this.gymId == null) {
            throw new IllegalArgumentException(
                    "El recepcionista debe estar asignado a un gimnasio.");
        }
    }

    public static Receptionist createNew(String firstName, String lastName, String email,
                                         String password, String dni, Long gymId) {
        return new Receptionist(null, firstName, lastName, email, password, dni, true,
                gymId);
    }

    public static Receptionist restore(Long id, String firstName, String lastName, String email,
                                       String password, String dni, boolean active,
                                       Long gymId) {
        return new Receptionist(id, firstName, lastName, email, password, dni, active,
                gymId);
    }

    public void updateProfile(String firstName, String lastName, String dni) {
        super.updateBaseDetails(firstName, lastName, dni);
    }
}
