package com.trainingapp.trainingapp.web.dto.access;

public record QrTokenResponse(String qrToken, int expiresInSeconds) {
}