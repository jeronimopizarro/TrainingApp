package com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails testUser;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Inyectamos la clave secreta manualmente para el entorno de test
        ReflectionTestUtils.setField(jwtService, "secretKey", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");

        testUser = new User("juan@test.com", "password123", Collections.emptyList());
    }

    @Test
    @DisplayName("Debería generar un token válido y extraer el email correctamente")
    void shouldGenerateTokenAndExtractUsername() {
        String token = jwtService.generateToken(testUser);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        String extractedUsername = jwtService.extractUsername(token);
        assertEquals("juan@test.com", extractedUsername);
    }

    @Test
    @DisplayName("Debería validar correctamente un token recién generado")
    void shouldValidateTokenSuccessfully() {
        String token = jwtService.generateToken(testUser);

        assertTrue(jwtService.isTokenValid(token, testUser));
    }

    @Test
    @DisplayName("Debería rechazar un token si el UserDetails pertenece a otro usuario")
    void shouldRejectTokenForDifferentUser() {
        String token = jwtService.generateToken(testUser);

        UserDetails differentUser = new User("otro@test.com", "pass", Collections.emptyList());

        assertFalse(jwtService.isTokenValid(token, differentUser));
    }

    @Test
    @DisplayName("Debería generar y validar un token QR de 60 segundos")
    void shouldGenerateAndExtractQrToken() {
        Long memberId = 15L;
        String qrToken = jwtService.generateQrToken(memberId);

        assertNotNull(qrToken);

        Long extractedId = jwtService.extractMemberIdFromQr(qrToken);
        assertEquals(memberId, extractedId);
    }
}