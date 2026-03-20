package com.trainingapp.trainingapp.web.dto.user.member;

public record MemberResponse(Long id, String firstName, String lastName, String email, String dni,
                             Long gymId, boolean active) {
}