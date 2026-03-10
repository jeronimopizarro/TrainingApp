package com.trainingapp.trainingapp.application.usecase.user.member;

import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.exception.user.MemberNotFoundException;
import com.trainingapp.trainingapp.domain.repository.user.MemberRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class DeleteMemberUseCase {

    private final MemberRepository memberRepository;

    public DeleteMemberUseCase(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void execute(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("Member with id " + id + " not found."));

        member.deactivate();

        memberRepository.save(member);
    }
}