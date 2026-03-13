package com.trainingapp.trainingapp.application.usecase.user.member;

import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.domain.exception.user.MemberNotFoundException;
import com.trainingapp.trainingapp.domain.repository.user.MemberRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.user.member.MemberResponse;
import com.trainingapp.trainingapp.web.dto.user.member.UpdateMemberRequest;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class UpdateMemberUseCase {

    private final MemberRepository memberRepository;
    private final SecurityUtils securityUtils;

    public UpdateMemberUseCase(MemberRepository memberRepository, SecurityUtils securityUtils) {
        this.memberRepository = memberRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public MemberResponse execute(Long id, UpdateMemberRequest request) {
        Member member = findMemberOrThrow(id);
        User currentUser = securityUtils.getCurrentUser();

        securityUtils.validateSameGym(member.getGymId());
        if (currentUser.getRole() == Role.MEMBER && !currentUser.getId().equals(member.getId())) {
            throw new AccessDeniedException("Solo puedes modificar tu propio perfil.");
        }

        member.updateProfile(request.firstName(), request.lastName(), request.primaryGoal());

        Member updatedMember = memberRepository.save(member);

        return buildResponseFromMember(updatedMember);
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