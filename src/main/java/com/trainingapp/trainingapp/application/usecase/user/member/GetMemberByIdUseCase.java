package com.trainingapp.trainingapp.application.usecase.user.member;

import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.domain.exception.user.MemberNotFoundException;
import com.trainingapp.trainingapp.domain.repository.user.MemberRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.user.member.MemberResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class GetMemberByIdUseCase {

    private final MemberRepository memberRepository;
    private final SecurityUtils securityUtils;

    public GetMemberByIdUseCase(MemberRepository memberRepository, SecurityUtils securityUtils) {
        this.memberRepository = memberRepository;
        this.securityUtils = securityUtils;
    }

    public MemberResponse execute(Long id) {
        User currentUser = securityUtils.getCurrentUser();

        validateAccess(currentUser, id);

        Member member = findMemberOrThrow(id);

        return buildResponseFromMember(member);
    }

    private void validateAccess(User currentUser, Long targetId) {
        if (currentUser.getRole() == Role.MEMBER && !currentUser.getId().equals(targetId)) {
            throw new AccessDeniedException("Solo puedes ver tu propio perfil.");
        }
    }

    private Member findMemberOrThrow(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(
                        () -> new MemberNotFoundException("Member with id " + id + " not found."));
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