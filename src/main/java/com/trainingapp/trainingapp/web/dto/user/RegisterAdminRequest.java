package com.trainingapp.trainingapp.web.dto.user;

import com.trainingapp.trainingapp.domain.enums.user.Role;

public record RegisterAdminRequest(String firstName, String lastName, String email, String password,
                                   Role role, Long gymId) {
}