package com.trainingapp.trainingapp.web.dto.user.member;

import java.time.LocalDate;

public record UpdateMemberRequest(String firstName, String lastName, String dni,
                                  LocalDate birthDate, String primaryGoal) {
}