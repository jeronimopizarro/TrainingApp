package com.trainingapp.trainingapp.domain.entity.user;

import com.trainingapp.trainingapp.domain.enums.user.Role;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
public class Member extends User {

    private final Long gymId;
    private LocalDate birthDate;
    private String primaryGoal;

    private Member(Long id, String firstName, String lastName, String email, String password,
                   String dni, Role role, boolean active, Long gymId,
                   LocalDate birthDate, String primaryGoal) {
        super(id, firstName, lastName, email, password, dni, role, active);

        this.gymId = gymId;
        this.birthDate = birthDate;
        this.primaryGoal = primaryGoal;
    }

    public static Member createNew(String firstName, String lastName, String email, String password,
                                   String dni,
                                   Long gymId, LocalDate birthDate, String primaryGoal) {
        return new Member(null, firstName, lastName, email, password, dni, Role.MEMBER, true, gymId,
                birthDate, primaryGoal);
    }

    public static Member restore(Long id, String firstName, String lastName, String email,
                                 String password, String dni, Role role, boolean active,
                                 Long gymId, LocalDate birthDate, String primaryGoal) {
        return new Member(id, firstName, lastName, email, password, dni, role, active, gymId,
                birthDate, primaryGoal);
    }

    public void updateMemberDetails(String firstName, String lastName, String dni,
                                    LocalDate birthDate, String primaryGoal) {
        super.updateBaseDetails(firstName, lastName, dni);
        this.birthDate = birthDate;
        this.primaryGoal = primaryGoal;
    }
}