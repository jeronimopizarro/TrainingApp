package com.trainingapp.trainingapp.domain.entity.user;

import com.trainingapp.trainingapp.domain.enums.user.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Trainer extends User{

    private Long gymId;
    private String specialization;

    public Trainer(String firstName, String lastName, String email, String password, Long gymId, String specialization){
        super(firstName, lastName, email, password, Role.TRAINER);

        if (gymId == null || gymId <= 0) {
            throw new IllegalArgumentException("A trainer must be associated with a valid Gym.");
        }

        this.gymId = gymId;
        this.specialization = specialization;
    }

    public void updateSpecialization(String newSpecialization) {
        if (newSpecialization == null || newSpecialization.isBlank()) {
            throw new IllegalArgumentException("Specialization cannot be empty.");
        }
        this.specialization = newSpecialization;
    }
}