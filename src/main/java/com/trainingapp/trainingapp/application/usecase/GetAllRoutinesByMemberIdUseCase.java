package com.trainingapp.trainingapp.application.usecase;

import com.trainingapp.trainingapp.domain.entity.Routine;
import com.trainingapp.trainingapp.domain.repository.RoutineRepository;
import com.trainingapp.trainingapp.web.dto.GetAllRoutinesByMemberIdResponse;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GetAllRoutinesByMemberIdUseCase {

    private final RoutineRepository routineRepository;

    public GetAllRoutinesByMemberIdUseCase(RoutineRepository routineRepository) {
        this.routineRepository = routineRepository;
    }

    public List<GetAllRoutinesByMemberIdResponse> execute(Long memberId) {
        List<Routine> routines = routineRepository.findAllByMemberId(memberId);

        return routines.stream().map(
                routine -> new GetAllRoutinesByMemberIdResponse(routine.getId(), routine.getName(),
                        routine.getStatus())).toList();
    }
}
