package com.trainingapp.trainingapp.domain.entity.user;

import com.trainingapp.trainingapp.domain.enums.user.Role;
import lombok.Getter;
import java.time.LocalDate;
import java.util.UUID;

@Getter
public class Member extends User {

    private Long gymId;
    private LocalDate birthDate;
    private String primaryGoal;

    public Member(String firstName, String lastName, String email, String password, String dni, Long gymId,
                     LocalDate birthDate, String primaryGoal) {
        super(firstName, lastName, email, password, dni, Role.MEMBER);

        if (gymId == null || gymId <= 0) {
            throw new IllegalArgumentException("A member must be associated with a valid Gym.");
        }
        this.gymId = gymId;
        this.birthDate = birthDate;
        this.primaryGoal = primaryGoal;
    }

    public void updateProfile(String firstName, String lastName, String primaryGoal) {
        super.updateBasicProfile(firstName, lastName);
        if (primaryGoal != null) {
            this.primaryGoal = primaryGoal;
        }
    }
}