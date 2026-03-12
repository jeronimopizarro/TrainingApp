package com.trainingapp.trainingapp.application.usecase.routine;

import com.trainingapp.trainingapp.application.validator.RoutineAccessValidator;
import com.trainingapp.trainingapp.domain.entity.routine.RoutineSummary;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.web.dto.routine.GetAllRoutinesByMemberIdResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllRoutinesByMemberIdUseCase {

    private final RoutineRepository routineRepository;
    private final RoutineAccessValidator accessValidator;

    public GetAllRoutinesByMemberIdUseCase(RoutineRepository routineRepository,
                                           RoutineAccessValidator accessValidator) {
        this.routineRepository = routineRepository;
        this.accessValidator = accessValidator;
    }

    public List<GetAllRoutinesByMemberIdResponse> execute(Long memberId) {
        accessValidator.validateTargetMemberAccess(memberId);

        List<RoutineSummary> summaries = routineRepository.findAllSummariesByMemberId(memberId);

        return mapToResponseList(summaries);
    }

    private List<GetAllRoutinesByMemberIdResponse> mapToResponseList(
            List<RoutineSummary> summaries) {
        return summaries.stream()
                .map(s -> new GetAllRoutinesByMemberIdResponse(s.id(), s.name(), s.status()))
                .toList();
    }
}