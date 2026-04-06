package com.trainingapp.trainingapp.application.useCase.user.member;

import com.trainingapp.trainingapp.application.validator.UserAccessValidator;
import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.exception.user.member.MemberNotFoundException;
import com.trainingapp.trainingapp.domain.repository.user.MemberRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class DeleteMemberUseCase {

    private final MemberRepository memberRepository;
    private final SecurityUtils securityUtils;
    private final UserAccessValidator userAccessValidator;

    public DeleteMemberUseCase(MemberRepository memberRepository, SecurityUtils securityUtils,
                               UserAccessValidator userAccessValidator) {
        this.memberRepository = memberRepository;
        this.securityUtils = securityUtils;
        this.userAccessValidator = userAccessValidator;
    }

    @Transactional
    public void execute(Long id) {
        Member member = findMemberOrThrow(id);

        securityUtils.validateSameGym(member.getGymId());
        userAccessValidator.validateWritePermission(member.getId());

        member.deactivate();
        memberRepository.save(member);
    }

    private Member findMemberOrThrow(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(
                        () -> new MemberNotFoundException(id));
    }
}