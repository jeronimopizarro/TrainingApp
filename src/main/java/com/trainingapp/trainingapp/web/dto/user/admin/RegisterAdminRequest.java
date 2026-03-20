package com.trainingapp.trainingapp.web.dto.user.admin;

import com.trainingapp.trainingapp.domain.enums.user.Role;

public record RegisterAdminRequest(String firstName, String lastName, String email, String password,
                                   String dni, Role role, Long gymId) {
}