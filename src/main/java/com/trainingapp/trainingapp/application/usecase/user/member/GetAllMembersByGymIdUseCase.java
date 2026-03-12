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
        securityUtils.validateSameGym(gymId);

        List<Member> members = memberRepository.findByGymId(gymId);

        return mapToResponseList(members);
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