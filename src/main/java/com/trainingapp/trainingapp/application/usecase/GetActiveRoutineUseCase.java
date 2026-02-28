package com.trainingapp.trainingapp.application.usecase;

import com.trainingapp.trainingapp.domain.Enum.RoutineStatus;
import com.trainingapp.trainingapp.domain.entity.Routine;
import com.trainingapp.trainingapp.domain.exception.RoutineNotFoundException;
import com.trainingapp.trainingapp.domain.repository.RoutineRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.RoutineResponseMapper;
import com.trainingapp.trainingapp.web.dto.RoutineResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class GetActiveRoutineUseCase {

    private final RoutineRepository routineRepository;
    private final RoutineResponseMapper mapper;

    public GetActiveRoutineUseCase(RoutineRepository routineRepository,
                                   RoutineResponseMapper mapper) {

        this.routineRepository = routineRepository;
        this.mapper = mapper;
    }

    public RoutineResponse execute(Long memberId) {
        Routine routine = routineRepository.findByMemberIdAndStatus(memberId,
                RoutineStatus.ACTIVE).orElseThrow(() -> new RoutineNotFoundException(
                "No active routine found for member with ID: " + memberId));

        return mapper.toDto(routine);
    }

}
