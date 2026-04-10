package com.trainingapp.trainingapp.application.useCase.access;

import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.exception.access.UnauthorizedQrGenerationException;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.JwtService;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.access.QrTokenResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerateAccessQrUseCaseTest {

    @Mock private SecurityUtils securityUtils;
    @Mock private JwtService jwtService;

    @InjectMocks private GenerateAccessQrUseCase useCase;

    @Test
    @DisplayName("Debería generar un token QR válido cuando el miembro solicita su propio QR")
    void shouldGenerateQrTokenForSameMember() {
        Long memberId = 100L;
        User mockUser = mock(User.class);
        String expectedToken = "qr_token_123";

        // Usuario autenticado tiene el mismo ID que el solicitado
        when(securityUtils.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getId()).thenReturn(memberId);

        when(jwtService.generateQrToken(memberId)).thenReturn(expectedToken);

        QrTokenResponse response = useCase.execute(memberId);

        assertNotNull(response);
        assertEquals(expectedToken, response.qrToken());
        assertEquals(60, response.expiresInSeconds());
    }

    @Test
    @DisplayName("Debería lanzar error si un usuario intenta generar un QR para el ID de otro miembro")
    void shouldThrowExceptionWhenUserTriesToGenerateQrForAnotherMember() {
        Long currentUserId = 99L;
        Long targetMemberId = 100L;
        User mockUser = mock(User.class);

        // Usuario autenticado es distinto al solicitado
        when(securityUtils.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getId()).thenReturn(currentUserId);

        assertThrows(UnauthorizedQrGenerationException.class, () -> useCase.execute(targetMemberId));

        // Verificamos que no se intentó generar ningún token
        verify(jwtService, never()).generateQrToken(anyLong());
    }
}