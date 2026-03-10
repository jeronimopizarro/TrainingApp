package com.trainingapp.trainingapp.application.usecase.user.member;

import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.repository.user.MemberRepository;
import com.trainingapp.trainingapp.web.dto.user.member.MemberResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllMembersByGymIdUseCase {

    private final MemberRepository memberRepository;

    public GetAllMembersByGymIdUseCase(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public List<MemberResponse> execute(Long gymId) {
        List<Member> members = memberRepository.findByGymId(gymId);

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