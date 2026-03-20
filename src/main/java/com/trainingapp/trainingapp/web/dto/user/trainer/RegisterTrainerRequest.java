package com.trainingapp.trainingapp.web.dto.user.trainer;

public record RegisterTrainerRequest(String firstName, String lastName, String email,
                                     String password, String dni, Long gymId, String specialization) {
}