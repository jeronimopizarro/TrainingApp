package com.trainingapp.trainingapp.application.useCase.access;

import com.trainingapp.trainingapp.domain.entity.Access.AccessLog;
import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.enums.access.AccessMethod;
import com.trainingapp.trainingapp.domain.exception.user.MemberNotFoundException;
import com.trainingapp.trainingapp.domain.repository.Access.AccessLogRepository;
import com.trainingapp.trainingapp.domain.repository.subscription.SubscriptionRepository;
import com.trainingapp.trainingapp.domain.repository.user.MemberRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.JwtService;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.access.ValidateAccessRequest;
import com.trainingapp.trainingapp.web.dto.access.ValidateAccessResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ValidateAccessUseCase {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final SecurityUtils securityUtils;
    private final SubscriptionRepository subscriptionRepository;
    private final AccessLogRepository accessLogRepository;

    public ValidateAccessUseCase(JwtService jwtService, MemberRepository memberRepository, SecurityUtils securityUtils,
                                 SubscriptionRepository subscriptionRepository,
                                 AccessLogRepository accessLogRepository) {
        this.jwtService = jwtService;
        this.memberRepository = memberRepository;
        this.securityUtils = securityUtils;
        this.subscriptionRepository = subscriptionRepository;
        this.accessLogRepository = accessLogRepository;
    }

    @Transactional
    public ValidateAccessResponse execute(ValidateAccessRequest request) {
        Long currentGymId = securityUtils.getCurrentUserGymId();

        try {
            Member member;

            if (request.method() == AccessMethod.QR) {
                Long memberId = jwtService.extractMemberIdFromQr(request.identifier());
                member = memberRepository.findById(memberId)
                        .orElseThrow(() -> new MemberNotFoundException(memberId));
            } else { //DNI
                member = memberRepository.findByDni(request.identifier().trim())
                        .orElseThrow(() -> new  MemberNotFoundException(Long.parseLong(
                                request.identifier().trim())));
            }

            return validateBusinessRules(member, securityUtils.getCurrentUserGymId());

        } catch (NumberFormatException e) {
            return logAndReturn(null, currentGymId, false, "Desconocido", "El identificador proporcionado no es válido.");
        } catch (MemberNotFoundException | IllegalArgumentException e) {
            return logAndReturn(null, currentGymId, false, "Desconocido", e.getMessage());
        } catch (ExpiredJwtException e) {
            return logAndReturn(null, currentGymId, false, "Desconocido", "El código QR expiró. Por favor, genere uno nuevo.");
        } catch (Exception e) {
            return logAndReturn(null, currentGymId, false, "Desconocido", "Código de acceso inválido.");
        }
    }

    private ValidateAccessResponse validateBusinessRules(Member member, Long currentGymId) {
        String fullName = member.getFirstName() + " " + member.getLastName();
        Long memberId = member.getId();

        if (!member.isActive()) {
            return logAndReturn(memberId, currentGymId, false, fullName, "El socio se encuentra dado de baja.");
        }

        if (!member.getGymId().equals(currentGymId)) {
            return logAndReturn(memberId, currentGymId, false, fullName, "El socio pertenece a otra sucursal.");
        }

        boolean hasActiveSubscription = subscriptionRepository.findActiveByMemberId(member.getId()).isPresent();
        if (!hasActiveSubscription) {
            return logAndReturn(memberId, currentGymId, false, fullName, "Cuota vencida o sin membresía activa.");
        }

        return logAndReturn(memberId, currentGymId, true, fullName, "Acceso permitido.");
    }

    private ValidateAccessResponse logAndReturn(Long memberId, Long gymId, boolean accessGranted, String memberName, String message) {
        AccessLog log = new AccessLog(memberId, gymId, LocalDateTime.now(), accessGranted, message);

        accessLogRepository.save(log);

        return new ValidateAccessResponse(accessGranted, memberName, message);
    }
}