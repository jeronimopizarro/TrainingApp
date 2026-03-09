package com.trainingapp.trainingapp.web.dto.user;

import java.time.LocalDate;

public record RegisterMemberRequest(String firstName, String lastName, String email,
                                    String password, Long gymId, LocalDate birthDate,
                                    String primaryGoal) {
}