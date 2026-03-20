package com.trainingapp.trainingapp.application.useCase.access;

import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.enums.access.AccessMethod;
import com.trainingapp.trainingapp.domain.repository.subscription.SubscriptionRepository;
import com.trainingapp.trainingapp.domain.repository.user.MemberRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.JwtService;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.access.ValidateAccessRequest;
import com.trainingapp.trainingapp.web.dto.access.ValidateAccessResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ValidateAccessUseCase {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final SecurityUtils securityUtils;
    private final SubscriptionRepository subscriptionRepository;

    public ValidateAccessUseCase(JwtService jwtService, MemberRepository memberRepository, SecurityUtils securityUtils,
                                 SubscriptionRepository subscriptionRepository) {
        this.jwtService = jwtService;
        this.memberRepository = memberRepository;
        this.securityUtils = securityUtils;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Transactional
    public ValidateAccessResponse execute(ValidateAccessRequest request) {
        Long currentGymId = securityUtils.getCurrentUserGymId();
        Member member;

        try {
            if (request.method() == AccessMethod.QR) {
                Long memberId = jwtService.extractMemberIdFromQr(request.identifier());

                Optional<Member> memberOpt = memberRepository.findById(memberId);
                if (memberOpt.isEmpty()) {
                    return new ValidateAccessResponse(false, "Desconocido", "Socio no encontrado.");
                }
                member = memberOpt.get();

            } else {
                Optional<Member> memberOpt = memberRepository.findByDni(request.identifier());
                if (memberOpt.isEmpty()) {
                    return new ValidateAccessResponse(false, "Desconocido", "No existe un socio con ese DNI en este gimnasio.");
                }
                member = memberOpt.get();
            }

        } catch (ExpiredJwtException e) {
            return new ValidateAccessResponse(false, "Desconocido", "El código QR expiró. Por favor, genere uno nuevo.");
        } catch (Exception e) {
            return new ValidateAccessResponse(false, "Desconocido", "Código de acceso inválido.");
        }

        String fullName = member.getFirstName() + " " + member.getLastName();

        if (!member.isActive()) {
            return new ValidateAccessResponse(false, fullName, "El socio se encuentra dado de baja.");
        }

        if (!member.getGymId().equals(currentGymId)) {
            return new ValidateAccessResponse(false, fullName, "El socio pertenece a otra sucursal.");
        }

        boolean hasActiveSubscription = subscriptionRepository.findActiveByMemberId(member.getId()).isPresent();

        if (!hasActiveSubscription) {
            return new ValidateAccessResponse(false, fullName, "Cuota vencida o sin membresía activa.");
        }

        return new ValidateAccessResponse(true, fullName, "Acceso permitido.");
    }
}