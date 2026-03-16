package com.trainingapp.trainingapp.application.useCase.user.member;

import com.trainingapp.trainingapp.application.mapper.member.MemberDTOMapper;
import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.repository.user.MemberRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.user.member.MemberResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllMembersByGymIdUseCase {

    private final MemberRepository memberRepository;
    private final SecurityUtils securityUtils;
    private final MemberDTOMapper memberDTOMapper;

    public GetAllMembersByGymIdUseCase(MemberRepository memberRepository,
                                       SecurityUtils securityUtils, MemberDTOMapper memberDTOMapper) {
        this.memberRepository = memberRepository;
        this.securityUtils = securityUtils;
        this.memberDTOMapper = memberDTOMapper;
    }

    public List<MemberResponse> execute(Long gymId) {
        securityUtils.validateSameGym(gymId);

        List<Member> members = memberRepository.findByGymId(gymId);

        return members.stream()
                .map(memberDTOMapper::toResponse)
                .toList();
    }
}