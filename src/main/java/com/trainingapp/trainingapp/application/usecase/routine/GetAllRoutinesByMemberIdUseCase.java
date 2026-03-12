package com.trainingapp.trainingapp.application.usecase.routine;

import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.entity.routine.RoutineSummary;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.web.dto.routine.GetAllRoutinesByMemberIdResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GetAllRoutinesByMemberIdUseCase {

    private final RoutineRepository routineRepository;

    public GetAllRoutinesByMemberIdUseCase(RoutineRepository routineRepository) {
        this.routineRepository = routineRepository;
    }

    public List<GetAllRoutinesByMemberIdResponse> execute(Long memberId) {
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