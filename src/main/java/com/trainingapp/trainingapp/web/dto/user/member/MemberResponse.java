package com.trainingapp.trainingapp.web.dto.user.member;

import java.time.LocalDate;

public record MemberResponse(Long id, String firstName, String lastName, String email, String dni,
                             boolean active, Long gymId, LocalDate birthDate, String primaryGoal) {
}