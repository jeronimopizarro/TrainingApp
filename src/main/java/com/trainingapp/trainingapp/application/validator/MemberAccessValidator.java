package com.trainingapp.trainingapp.application.validator;

import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.exception.user.MemberAccessDeniedException;
import com.trainingapp.trainingapp.domain.exception.user.MemberNotFoundException;
import com.trainingapp.trainingapp.domain.repository.user.MemberRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import org.springframework.stereotype.Component;

@Component
public class MemberAccessValidator {

    private final MemberRepository memberRepository;
    private final SecurityUtils securityUtils;

    public MemberAccessValidator(MemberRepository memberRepository, SecurityUtils securityUtils) {
        this.memberRepository = memberRepository;
        this.securityUtils = securityUtils;
    }

    public Member findMemberAndValidateAccess(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        // Esto es seguro: Si es SUPER_ADMIN lo deja pasar. Si es GYM_ADMIN comprueba el ID.
        securityUtils.validateSameGym(member.getGymId());

        return member;
    }
}