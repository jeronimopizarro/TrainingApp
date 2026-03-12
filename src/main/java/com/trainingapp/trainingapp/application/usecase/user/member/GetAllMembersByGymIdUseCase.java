package com.trainingapp.trainingapp.application.usecase.user.member;

import com.trainingapp.trainingapp.domain.entity.user.Admin;
import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.repository.user.MemberRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.user.member.MemberResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllMembersByGymIdUseCase {

    private final MemberRepository memberRepository;
    private final SecurityUtils securityUtils;

    public GetAllMembersByGymIdUseCase(MemberRepository memberRepository,
                                       SecurityUtils securityUtils) {
        this.memberRepository = memberRepository;
        this.securityUtils = securityUtils;
    }

    public List<MemberResponse> execute(Long gymId) {
        User currentUser = securityUtils.getCurrentUser();

        validateGymAccess(currentUser, gymId);

        List<Member> members = memberRepository.findByGymId(gymId);

        return mapToResponseList(members);
    }

    private void validateGymAccess(User currentUser, Long targetGymId) {
        Long staffGymId = null;

        // Extraemos el Gym ID dependiendo de qué tipo de empleado sea.
        if (currentUser instanceof Admin admin) {
            staffGymId = admin.getGymId();
        } else if (currentUser instanceof Trainer trainer) {
            staffGymId = trainer.getGymId();
        }

        // Si es un empleado (tiene gymId) y no coincide con el buscado, lo bloqueamos
        if (staffGymId != null && !staffGymId.equals(targetGymId)) {
            throw new AccessDeniedException("No tienes permiso para ver los socios de otro gimnasio.");
        }
    }

    private List<MemberResponse> mapToResponseList(List<Member> members) {
        return members.stream()
                .map(this::buildResponseFromMember)
                .toList();
    }

    private MemberResponse buildResponseFromMember(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getFirstName(),
                member.getLastName(),
                member.getEmail(),
                member.getGymId(),
                member.getQrAccessCode(),
                member.isActive()
        );
    }
}