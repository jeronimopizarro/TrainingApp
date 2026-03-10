package com.trainingapp.trainingapp.application.usecase.user.member;

import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.exception.user.MemberNotFoundException;
import com.trainingapp.trainingapp.domain.repository.user.MemberRepository;
import com.trainingapp.trainingapp.web.dto.user.member.MemberResponse;
import com.trainingapp.trainingapp.web.dto.user.member.UpdateMemberRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UpdateMemberUseCase {

    private final MemberRepository memberRepository;

    public UpdateMemberUseCase(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public MemberResponse execute(Long id, UpdateMemberRequest request) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("Member with id " + id + " not found."));

        updateMemberFields(member, request);

        Member updatedMember = memberRepository.save(member);

        return buildResponseFromMember(updatedMember);
    }

    private void updateMemberFields(Member member, UpdateMemberRequest request) {
        if (request.firstName() != null && !request.firstName().isBlank()) {
            member.setFirstName(request.firstName());
        }
        if (request.lastName() != null && !request.lastName().isBlank()) {
            member.setLastName(request.lastName());
        }
        if (request.primaryGoal() != null) {
            member.updateGoal(request.primaryGoal());
        }
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