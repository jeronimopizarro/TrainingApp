package com.trainingapp.trainingapp.application.usecase.user.member;

import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.repository.user.MemberRepository;
import com.trainingapp.trainingapp.web.dto.user.member.MemberResponse;
import com.trainingapp.trainingapp.web.dto.user.member.RegisterMemberRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class RegisterMemberUseCase {
    private final MemberRepository memberRepository;

    public RegisterMemberUseCase(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public MemberResponse execute(RegisterMemberRequest request) {
        Member member = buildMemberFromRequest(request);

        Member savedMember = memberRepository.save(member);

        return buildResponseFromMember(savedMember);
    }

    private Member buildMemberFromRequest(RegisterMemberRequest request) {
        return new Member(
                request.firstName(),
                request.lastName(),
                request.email(),
                request.password(),
                request.gymId(),
                request.birthDate(),
                request.primaryGoal()
        );
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
