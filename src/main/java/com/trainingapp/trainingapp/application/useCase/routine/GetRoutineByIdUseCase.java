package com.trainingapp.trainingapp.application.useCase.routine;

import com.trainingapp.trainingapp.application.mapper.routine.RoutineDTOMapper;
import com.trainingapp.trainingapp.application.validator.RoutineAccessValidator;
import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.entity.routine.RoutineDetail;
import com.trainingapp.trainingapp.domain.exception.routine.RoutineNotFoundException;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.web.dto.routine.RoutineDetailResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetRoutineByIdUseCase {

    private final RoutineRepository routineRepository;
    private final ExerciseRepository exerciseRepository;
    private final RoutineAccessValidator accessValidator;
    private final RoutineDTOMapper routineDTOMapper;

    public GetRoutineByIdUseCase(RoutineRepository routineRepository,
                                 ExerciseRepository exerciseRepository,
                                 RoutineAccessValidator accessValidator,
                                 RoutineDTOMapper routineDTOMapper) {
        this.routineRepository = routineRepository;
        this.exerciseRepository = exerciseRepository;
        this.accessValidator = accessValidator;
        this.routineDTOMapper = routineDTOMapper;
    }

    public RoutineDetailResponse execute(Long id) {
        Routine routine = findRoutineOrThrow(id);

        accessValidator.validateReadPermission(routine);
        List<Exercise> exerciseCatalog = fetchExerciseCatalog(routine);

        return routineDTOMapper.toRoutineDetailResponse(routine, exerciseCatalog);
    }

    private Routine findRoutineOrThrow(Long id) {
        return routineRepository.findById(id)
                .orElseThrow(() -> new RoutineNotFoundException(id));
    }

    private List<Exercise> fetchExerciseCatalog(Routine routine) {
        List<Long> exerciseIds = extractExerciseIds(routine);
        return exerciseRepository.findAllById(exerciseIds);
    }

    private List<Long> extractExerciseIds(Routine routine) {
        return routine.getDays().stream()
                .flatMap(day -> day.getDetails().stream())
                .map(RoutineDetail::getExerciseId)
                .distinct()
                .toList();
    }
}