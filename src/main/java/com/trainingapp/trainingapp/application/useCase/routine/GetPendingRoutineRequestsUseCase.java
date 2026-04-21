package com.trainingapp.trainingapp.application.useCase.routine;

import com.trainingapp.trainingapp.domain.entity.routine.RoutineRequest;
import com.trainingapp.trainingapp.domain.enums.routine.RoutineRequestStatus;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRequestRepository;
import com.trainingapp.trainingapp.domain.repository.user.MemberRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.web.dto.routine.GetPendingRoutineRequestsResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

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
        Long currentUserId = securityUtils.getCurrentUser().getId();

        // 1. Buscamos todas las PENDING del gimnasio (para el tab Global)
        List<RoutineRequest> pendingRequests = routineRequestRepository.findByGymIdAndStatus(currentGymId, RoutineRequestStatus.PENDING);

        // 2. Buscamos todas las IN_PROGRESS asignadas a ESTE entrenador (para que no las pierda)
        List<RoutineRequest> inProgressRequests = routineRequestRepository.findByAssignedTrainerIdAndStatus(currentUserId, RoutineRequestStatus.IN_PROGRESS);

        // Combinamos ambas listas
        List<RoutineRequest> allVisibleRequests = Stream.concat(pendingRequests.stream(), inProgressRequests.stream())
                .toList();

        return allVisibleRequests.stream()
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
