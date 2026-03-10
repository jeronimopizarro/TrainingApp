package com.trainingapp.trainingapp.application.usecase.user.member;

import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.exception.user.MemberNotFoundException;
import com.trainingapp.trainingapp.domain.repository.user.MemberRepository;
import com.trainingapp.trainingapp.web.dto.user.member.MemberResponse;
import org.springframework.stereotype.Service;

@Service
public class GetMemberByIdUseCase {

    private final MemberRepository memberRepository;

    public GetMemberByIdUseCase(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public MemberResponse execute(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("Member with id " + id + " not found."));

        return buildResponseFromMember(member);
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