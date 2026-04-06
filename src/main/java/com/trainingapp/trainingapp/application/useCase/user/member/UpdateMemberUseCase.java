package com.trainingapp.trainingapp.application.useCase.user.member;

import com.trainingapp.trainingapp.application.mapper.member.MemberDTOMapper;
import com.trainingapp.trainingapp.application.validator.UserAccessValidator;
import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.exception.user.member.MemberNotFoundException;
import com.trainingapp.trainingapp.domain.repository.user.MemberRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.user.member.MemberResponse;
import com.trainingapp.trainingapp.web.dto.user.member.UpdateMemberRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UpdateMemberUseCase {

    private final MemberRepository memberRepository;
    private final SecurityUtils securityUtils;
    private final MemberDTOMapper memberDTOMapper;
    private final UserAccessValidator userAccessValidator;

    public UpdateMemberUseCase(MemberRepository memberRepository, SecurityUtils securityUtils,
                               MemberDTOMapper memberDTOMapper,
                               UserAccessValidator userAccessValidator) {
        this.memberRepository = memberRepository;
        this.securityUtils = securityUtils;
        this.memberDTOMapper = memberDTOMapper;
        this.userAccessValidator = userAccessValidator;
    }

    @Transactional
    public MemberResponse execute(Long id, UpdateMemberRequest request) {
        Member member = findMemberOrThrow(id);

        securityUtils.validateSameGym(member.getGymId());
        userAccessValidator.validateWritePermission(member.getId());

        member.updateMemberDetails(request.firstName(), request.lastName(), request.dni(),
                request.birthDate(), request.primaryGoal());

        Member updatedMember = memberRepository.save(member);
        return memberDTOMapper.toResponse(updatedMember);
    }

    private Member findMemberOrThrow(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new MemberNotFoundException(id));
    }
}