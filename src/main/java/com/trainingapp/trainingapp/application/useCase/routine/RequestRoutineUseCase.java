package com.trainingapp.trainingapp.application.useCase.routine;

import com.trainingapp.trainingapp.domain.entity.routine.RoutineRequest;
import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.enums.routine.RoutineRequestStatus;
import com.trainingapp.trainingapp.domain.exception.routine.ActiveRoutineRequestAlreadyExistsException;
import com.trainingapp.trainingapp.domain.exception.user.member.MemberNotFoundException;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRequestRepository;
import com.trainingapp.trainingapp.domain.repository.user.MemberRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.routine.RequestRoutineMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RequestRoutineUseCase {

    private final RoutineRequestRepository routineRequestRepository;
    private final MemberRepository memberRepository;
    private final SecurityUtils securityUtils;

    public RequestRoutineUseCase(RoutineRequestRepository routineRequestRepository,
                                 MemberRepository memberRepository,
                                 SecurityUtils securityUtils) {
        this.routineRequestRepository = routineRequestRepository;
        this.memberRepository = memberRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public void execute(RequestRoutineMessage request) {
        Long memberId = securityUtils.getCurrentUser().getId();

        validateNoPendingRequestExists(memberId);
        Member member = findMemberById(memberId);

        RoutineRequest newRequest = createRoutineRequest(request, member);
        routineRequestRepository.save(newRequest);
    }

    private void validateNoPendingRequestExists(Long memberId) {
        if (routineRequestRepository.existsByMemberIdAndStatus(memberId, RoutineRequestStatus.PENDING)) {
            throw new ActiveRoutineRequestAlreadyExistsException(memberId);
        }
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
    }

    private RoutineRequest createRoutineRequest(RequestRoutineMessage request, Member member) {
        return RoutineRequest.createNew(
                member.getId(),
                member.getGymId(),
                request.targetTrainerId(),
                request.availableDays(),
                request.experienceLevel(),
                request.injuries(),
                request.primaryGoal()
        );
    }
}
