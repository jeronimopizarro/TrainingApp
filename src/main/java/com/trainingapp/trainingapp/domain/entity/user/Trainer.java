package com.trainingapp.trainingapp.domain.entity.user;

import com.trainingapp.trainingapp.domain.enums.user.Role;
import lombok.Getter;

@Getter
public class Trainer extends User {

    private final Long gymId;
    private String specialization;

    private Trainer(Long id, String firstName, String lastName, String email, String password,
                    String dni, Role role, boolean active,
                    Long gymId, String specialization) {
        super(id, firstName, lastName, email, password, dni, role, active);

        this.gymId = gymId;
        this.specialization = specialization;
    }

    public static Trainer createNew(String firstName, String lastName, String email,
                                    String password, String dni,
                                    Long gymId, String specialization) {
        return new Trainer(null, firstName, lastName, email, password, dni, Role.TRAINER, true, gymId,
                specialization);
    }

    public static Trainer restore(Long id, String firstName, String lastName, String email,
                                  String password, String dni, Role role, boolean active,
                                  Long gymId, String specialization) {
        return new Trainer(id, firstName, lastName, email, password, dni, role, active, gymId,
                specialization);
    }

    public void updateTrainerDetails(String firstName, String lastName, String dni,
                                     String specialization) {
        super.updateBaseDetails(firstName, lastName, dni);
        this.specialization = specialization;
    }
}