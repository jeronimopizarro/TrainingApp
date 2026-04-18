package com.trainingapp.trainingapp.application.useCase.routine;

import com.trainingapp.trainingapp.domain.entity.routine.RoutineRequest;
import com.trainingapp.trainingapp.domain.enums.routine.RoutineRequestStatus;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRequestRepository;
import com.trainingapp.trainingapp.domain.repository.user.MemberRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.routine.GetPendingRoutineRequestsResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetPendingRoutineRequestsUseCase {

    private final RoutineRequestRepository routineRequestRepository;
    private final MemberRepository memberRepository;
    private final SecurityUtils securityUtils;

    public GetPendingRoutineRequestsUseCase(RoutineRequestRepository routineRequestRepository,
                                            MemberRepository memberRepository,
                                            SecurityUtils securityUtils) {
        this.routineRequestRepository = routineRequestRepository;
        this.memberRepository = memberRepository;
        this.securityUtils = securityUtils;
    }

    public List<GetPendingRoutineRequestsResponse> execute() {
        Long currentGymId = securityUtils.getCurrentUserGymId();

        List<RoutineRequest> requests = routineRequestRepository.findByGymIdAndStatus(currentGymId, RoutineRequestStatus.PENDING);

        return requests.stream()
                .map(request -> {
                    String memberName = memberRepository.findById(request.getMemberId())
                            .map(member -> member.getFirstName() + " " + member.getLastName())
                            .orElse("Unknown Member");

                    return new GetPendingRoutineRequestsResponse(
                            request.getId(),
                            request.getMemberId(),
                            memberName,
                            request.getRequestDate(),
                            request.getStatus(),
                            request.getTargetTrainerId(),
                            request.getAvailableDays(),
                            request.getExperienceLevel(),
                            request.getInjuries(),
                            request.getPrimaryGoal()
                    );
                })
                .toList();
    }
}
