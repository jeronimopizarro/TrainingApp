package com.trainingapp.trainingapp.application.useCase.user.member;

import com.trainingapp.trainingapp.application.mapper.member.MemberDTOMapper;
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
                        () -> new MemberNotFoundException("Member with id " + id + " not found."));
    }

    private void validateReadPermission(User currentUser, Member targetMember) {
        boolean isSuperAdmin = currentUser.getRole() == Role.SUPER_ADMIN;
        boolean isAdmin = currentUser.getRole() == Role.GYM_ADMIN;
        boolean isTrainer = currentUser.getRole() == Role.TRAINER;
        boolean isSelfMember = currentUser.getRole() == Role.MEMBER && currentUser.getId().equals(targetMember.getId());

        if (!isSuperAdmin && !isAdmin && !isTrainer && !isSelfMember) {
            throw new AccessDeniedException("No tienes permisos para ver el perfil de este socio.");
        }
    }
}