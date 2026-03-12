package com.trainingapp.trainingapp.application.usecase.routine;

import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.entity.routine.RoutineDetail;
import com.trainingapp.trainingapp.domain.entity.routine.TrainingDay;
import com.trainingapp.trainingapp.domain.exception.routine.RoutineNotFoundException;
import com.trainingapp.trainingapp.domain.repository.exercise.ExerciseRepository;
import com.trainingapp.trainingapp.domain.repository.routine.RoutineRepository;
import com.trainingapp.trainingapp.web.dto.routine.RoutineDetailResponse;
import com.trainingapp.trainingapp.web.dto.routine.RoutineDetailResponse.ExerciseItemResponse;
import com.trainingapp.trainingapp.web.dto.routine.RoutineDetailResponse.DayDetailResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GetRoutineByIdUseCase {

    private final RoutineRepository routineRepository;
    private final ExerciseRepository exerciseRepository;

    public GetRoutineByIdUseCase(RoutineRepository routineRepository,
                                 ExerciseRepository exerciseRepository) {
        this.routineRepository = routineRepository;
        this.exerciseRepository = exerciseRepository;
    }

    public RoutineDetailResponse execute(Long id) {
        Routine routine = findRoutineOrThrow(id);

        List<Exercise> exerciseCatalog = fetchExerciseCatalog(routine);

        List<DayDetailResponse> hydratedDays = mapToHydratedDays(routine, exerciseCatalog);

        return mapToDetailResponse(routine, hydratedDays);
    }

    private Routine findRoutineOrThrow(Long id) {
        return routineRepository.findById(id)
                .orElseThrow(() -> new RoutineNotFoundException(
                        "The routine with id " + id + " was not found"));
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

    private List<DayDetailResponse> mapToHydratedDays(Routine routine, List<Exercise> catalog) {
        return routine.getDays().stream()
                .map(day -> mapToDayResponse(day, catalog))
                .toList();
    }

    private DayDetailResponse mapToDayResponse(TrainingDay day, List<Exercise> catalog) {
        List<ExerciseItemResponse> exercises = day.getDetails().stream()
                .map(detail -> mapToExerciseItemResponse(detail, catalog))
                .toList();

        return new DayDetailResponse(day.getId(), day.getName(), day.getOrderNumber(), exercises);
    }

    private ExerciseItemResponse mapToExerciseItemResponse(RoutineDetail detail, List<Exercise> catalog) {
        // Buscamos el ejercicio en el catálogo local (para no ir a la DB en cada iteración)
        Exercise catalogData = catalog.stream()
                .filter(e -> e.getId().equals(detail.getExerciseId()))
                .findFirst()
                .orElse(null);

        return buildExerciseItem(detail, catalogData);
    }

    private ExerciseItemResponse buildExerciseItem(RoutineDetail detail, Exercise data) {
        String name = (data != null) ? data.getName() : "Ejercicio Borrado";
        String img = (data != null) ? data.getImageUrl() : null;
        String video = (data != null) ? data.getVideoUrl() : null;

        return new ExerciseItemResponse(
                detail.getOrderNumber(), detail.getSets(), detail.getRepsMin(), detail.getRepsMax(),
                detail.getTargetRIR(), detail.getSuggestedWeight(), detail.getNotes(),
                detail.getExerciseId(), name, img, video
        );
    }

    private RoutineDetailResponse mapToDetailResponse(Routine routine, List<DayDetailResponse> days) {
        return new RoutineDetailResponse(
                routine.getId(), routine.getName(), routine.getStartDate(), routine.getEndDate(),
                routine.getMemberId(), routine.getTrainerId(), routine.getCreatedByUserId(),
                routine.getStatus(), days
        );
    }
}