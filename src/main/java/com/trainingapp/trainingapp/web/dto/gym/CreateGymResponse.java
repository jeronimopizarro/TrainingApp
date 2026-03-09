package com.trainingapp.trainingapp.web.dto.gym;

public record CreateGymResponse(Long id, String name, String address, String phone, boolean active) {
}