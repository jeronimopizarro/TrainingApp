package com.trainingapp.trainingapp.application.useCase.user.member;

import com.trainingapp.trainingapp.application.mapper.member.MemberDTOMapper;
import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.exception.user.member.MemberNotFoundException;
import com.trainingapp.trainingapp.domain.exception.user.UnauthorizedProfileAccessException;
import com.trainingapp.trainingapp.domain.repository.user.MemberRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.user.member.MemberResponse;
import org.springframework.stereotype.Service;

@Service
public class GetMemberByIdUseCase {

    private final MemberRepository memberRepository;
    private final SecurityUtils securityUtils;
    private final MemberDTOMapper memberDTOMapper;

    public GetMemberByIdUseCase(MemberRepository memberRepository, SecurityUtils securityUtils,
                                MemberDTOMapper memberDTOMapper) {
        this.memberRepository = memberRepository;
        this.securityUtils = securityUtils;
        this.memberDTOMapper = memberDTOMapper;
    }

    public MemberResponse execute(Long id) {
        Member member = findMemberOrThrow(id);
        User currentUser = securityUtils.getCurrentUser();

        securityUtils.validateSameGym(member.getGymId());
        validateReadPermission(currentUser, member);

        return memberDTOMapper.toResponse(member);
    }

    private Member findMemberOrThrow(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(
                        () -> new MemberNotFoundException(id));
    }

    private void validateReadPermission(User currentUser, Member targetMember) {
        boolean isSuperAdmin = currentUser.isSuperAdmin();
        boolean isAdmin = currentUser.isGymAdmin();
        boolean isTrainer = currentUser.isTrainer();
        boolean isSelfMember = currentUser.isMember() && currentUser.getId().equals(targetMember.getId());

        if (!isSuperAdmin && !isAdmin && !isTrainer && !isSelfMember) {
            throw new UnauthorizedProfileAccessException();
        }
    }
}
