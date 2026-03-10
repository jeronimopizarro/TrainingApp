package com.trainingapp.trainingapp.web.dto.user.admin;

import com.trainingapp.trainingapp.domain.enums.user.Role;

public record AdminResponse(Long id, String firstName, String lastName, String email, Role role,
                            Long gymId, boolean active) {
}