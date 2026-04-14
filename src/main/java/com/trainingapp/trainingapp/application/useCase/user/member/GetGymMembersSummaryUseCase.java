package com.trainingapp.trainingapp.application.useCase.user.member;

import com.trainingapp.trainingapp.domain.entity.subscription.Subscription;
import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.repository.subscription.SubscriptionRepository;
import com.trainingapp.trainingapp.domain.repository.user.MemberRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.user.member.MemberSummaryResponse;
import com.trainingapp.trainingapp.web.dto.user.member.MemberSummaryResponse.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GetGymMembersSummaryUseCase {

    private final MemberRepository memberRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SecurityUtils securityUtils;

    public GetGymMembersSummaryUseCase(MemberRepository memberRepository,
                                       SubscriptionRepository subscriptionRepository,
                                       SecurityUtils securityUtils) {
        this.memberRepository = memberRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional(readOnly = true)
    public MemberSummaryResponse execute(Long gymId, String statusFilter) {
        securityUtils.validateSameGym(gymId);

        // Obtenemos todos los socios del gimnasio (lista base)
        List<Member> allMembers = memberRepository.findByGymId(gymId);
        
        // Enriquecemos cada socio con su suscripción y filtramos de forma inteligente
        List<MemberListItem> items = allMembers.stream()
                .map(this::mapToListItem)
                .filter(item -> {
                    if (statusFilter == null || statusFilter.isBlank()) return true;
                    if ("ACTIVE".equalsIgnoreCase(statusFilter)) {
                        return "ACTIVE".equals(item.subscriptionStatus());
                    }
                    if ("INACTIVE".equalsIgnoreCase(statusFilter)) {
                        return !"ACTIVE".equals(item.subscriptionStatus());
                    }
                    return item.subscriptionStatus().equalsIgnoreCase(statusFilter);
                })
                .toList();

        // Calculamos estadísticas globales sobre la lista COMPLETA (no la filtrada)
        long total = allMembers.size();
        long active = allMembers.stream()
                .map(this::mapToListItem)
                .filter(item -> "ACTIVE".equals(item.subscriptionStatus()))
                .count();

        return new MemberSummaryResponse(
                new MemberStats(total, active, total - active),
                items
        );
    }

    private MemberListItem mapToListItem(Member member) {
        Optional<Subscription> subOpt = subscriptionRepository.findActiveByMemberId(member.getId());
        
        return new MemberListItem(
                member.getId(),
                member.getFirstName(),
                member.getLastName(),
                member.getEmail(),
                member.getDni(),
                subOpt.map(Subscription::getPlanName).orElse("Sin plan activo"),
                subOpt.map(s -> s.getStatus().name()).orElse("NONE"),
                subOpt.map(Subscription::getEndDate).orElse(null)
        );
    }
}
