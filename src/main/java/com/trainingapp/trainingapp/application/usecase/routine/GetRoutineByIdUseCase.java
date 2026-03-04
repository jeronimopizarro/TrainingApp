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
        Routine routine = validateRoutine(id);

        List<Long> exerciseIds = getExerciseIds(routine);

        List<Exercise> exerciseCatalog = exerciseRepository.findAllById(exerciseIds);

        List<DayDetailResponse> hydratedDays = hydrateDays(routine, exerciseCatalog);

        return new RoutineDetailResponse(
                routine.getId(), routine.getName(), routine.getStartDate(), routine.getEndDate(),
                routine.getMemberId(), routine.getTrainerId(), routine.getCreatedByUserId(),
                routine.getStatus(), hydratedDays
        );
    }

    private Routine validateRoutine(Long id) {
        return routineRepository.findById(id)
                .orElseThrow(() -> new RoutineNotFoundException(
                        "The routine with id " + id + " was not found"));
    }

    private static @NonNull List<Long> getExerciseIds(Routine routine) {
        List<Long> exerciseIds = new ArrayList<>();
        for (TrainingDay day : routine.getDays()) {
            for (RoutineDetail detail : day.getDetails()) {
                if (!exerciseIds.contains(detail.getExerciseId())) {
                    exerciseIds.add(detail.getExerciseId());
                }
            }
        }
        return exerciseIds;
    }

    private Exercise findExerciseInList(List<Exercise> catalog, Long exerciseId) {
        for (Exercise exercise : catalog) {
            if (exercise.getId().equals(exerciseId)) {
                return exercise;
            }
        }
        return null;
    }

    private List<DayDetailResponse> hydrateDays(Routine routine, List<Exercise> exerciseCatalog) {
        List<DayDetailResponse> hydratedDays = new ArrayList<>();
        for (TrainingDay day : routine.getDays()) {
            List<ExerciseItemResponse> hydratedExercises = new ArrayList<>();

            for (RoutineDetail detail : day.getDetails()) {
                Exercise catalogData = findExerciseInList(exerciseCatalog, detail.getExerciseId());

                String exerciseName = (catalogData != null) ? catalogData.getName() : "Ejercicio Borrado";
                String imageUrl = (catalogData != null) ? catalogData.getImageUrl() : null;
                String videoUrl = (catalogData != null) ? catalogData.getVideoUrl() : null;

                ExerciseItemResponse exResponse = new ExerciseItemResponse(
                        detail.getOrderNumber(), detail.getSets(), detail.getRepsMin(),
                        detail.getRepsMax(),
                        detail.getTargetRIR(), detail.getSuggestedWeight(), detail.getNotes(),
                        detail.getExerciseId(), exerciseName, imageUrl, videoUrl
                );

                hydratedExercises.add(exResponse);
            }

            DayDetailResponse dayResponse = new DayDetailResponse(
                    day.getId(), day.getName(), day.getOrderNumber(), hydratedExercises
            );
            hydratedDays.add(dayResponse);
        }
        return hydratedDays;
    }
}