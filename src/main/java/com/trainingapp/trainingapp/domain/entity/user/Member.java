package com.trainingapp.trainingapp.domain.entity.user;

import com.trainingapp.trainingapp.domain.enums.user.Role;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class Member extends User {

    private Long gymId;
    private LocalDate birthDate;
    private String primaryGoal;
    private String qrAccessCode;

    public Member(String firstName, String lastName, String email, String password, Long gymId,
                     LocalDate birthDate, String primaryGoal) {
        super(firstName, lastName, email, password, Role.MEMBER);

        if (gymId == null || gymId <= 0) {
            throw new IllegalArgumentException("A member must be associated with a valid Gym.");
        }
        this.gymId = gymId;
        this.birthDate = birthDate;
        this.primaryGoal = primaryGoal;
        this.qrAccessCode = generateQrCode();
    }

    private String generateQrCode() {
        return "QR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public void updateGoal(String newGoal) {
        this.primaryGoal = newGoal;
    }
}