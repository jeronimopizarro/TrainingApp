package com.trainingapp.trainingapp.application.useCase.access;

import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.JwtService;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.access.QrTokenResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class GenerateAccessQrUseCase {

    private final JwtService jwtService;
    private final SecurityUtils securityUtils;

    public GenerateAccessQrUseCase(JwtService jwtService, SecurityUtils securityUtils) {
        this.jwtService = jwtService;
        this.securityUtils = securityUtils;
    }

    public QrTokenResponse execute(Long memberId) {
        User currentUser = securityUtils.getCurrentUser();
        validateIsSameMember(currentUser, memberId);

        String qrToken = jwtService.generateQrToken(memberId);
        return new  QrTokenResponse(qrToken, 60);
    }

    private void validateIsSameMember(User currentUser, Long targetMemberId) {
        if (!currentUser.getId().equals(targetMemberId)) {
            throw new AccessDeniedException("Acceso denegado: No puedes generar un código de acceso para otro socio.");
        }
    }
}