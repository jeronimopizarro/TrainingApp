package com.trainingapp.trainingapp.web.dto.user;

public record TrainerResponse(Long id, String firstName, String lastName, String email, Long gymId,
                              String specialization, boolean active) {
}