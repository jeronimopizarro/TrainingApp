package com.trainingapp.trainingapp.application.mapper.routine;

import com.trainingapp.trainingapp.domain.entity.exercise.Exercise;
import com.trainingapp.trainingapp.domain.entity.routine.Routine;
import com.trainingapp.trainingapp.domain.entity.routine.RoutineDetail;
import com.trainingapp.trainingapp.domain.entity.routine.RoutineSummary;
import com.trainingapp.trainingapp.domain.entity.routine.TrainingDay;
import com.trainingapp.trainingapp.web.dto.routine.RoutineDetailResponse;
import com.trainingapp.trainingapp.web.dto.routine.RoutineDetailResponse.DayDetailResponse;
import com.trainingapp.trainingapp.web.dto.routine.RoutineDetailResponse.ExerciseItemResponse;
import com.trainingapp.trainingapp.web.dto.routine.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoutineDTOMapper {

    public Routine toDomain(CreateRoutineRequest request, Long creatorId, Long gymId) {
        if (request == null) return null;

        Routine routine = new Routine(request.name(), request.memberId(), request.trainerId(), creatorId, gymId);

        if (request.days() != null) {
            request.days().forEach(dayRequest -> {
                TrainingDay createdDay = routine.addDay(dayRequest.dayName());

                if (dayRequest.exercises() != null) {
                    dayRequest.exercises().forEach(exerciseReq -> {
                        createdDay.addDetails(
                                exerciseReq.exerciseId(),
                                exerciseReq.sets(),
                                exerciseReq.repsMin(),
                                exerciseReq.repsMax(),
                                exerciseReq.targetRIR(),
                                exerciseReq.suggestedWeight(),
                                exerciseReq.notes()
                        );
                    });
                }
            });
        }
        return routine;
    }

    public List<TrainingDay> toDomainDays(
            List<UpdateRoutineRequest.UpdateTrainingDayRequest> dayRequests) {
        if (dayRequests == null) return new java.util.ArrayList<>();

        List<TrainingDay> domainDays = new java.util.ArrayList<>();
        int dayOrder = 1;

        for (UpdateRoutineRequest.UpdateTrainingDayRequest dayReq : dayRequests) {
            TrainingDay day = new TrainingDay(dayReq.dayName(), dayOrder++);
            day.setId(dayReq.id());

            if (dayReq.exercises() != null) {
                for (UpdateRoutineRequest.UpdateRoutineDetailRequest exReq : dayReq.exercises()) {
                    RoutineDetail detail = new RoutineDetail(
                            exReq.exerciseId(),
                            0, // El orden se recalcula adentro de Routine
                            exReq.sets(), exReq.repsMin(), exReq.repsMax(),
                            exReq.targetRIR(), exReq.suggestedWeight(), exReq.notes()
                    );
                    detail.setId(exReq.id());
                    day.getDetails().add(detail);
                }
            }
            domainDays.add(day);
        }
        return domainDays;
    }

    public CreateRoutineResponse toResponse(Routine routine, String message) {
        if (routine == null) return null;
        return new CreateRoutineResponse(routine.getId(), message);
    }

    public GetAllRoutinesByMemberIdResponse toAllRoutinesByMemberIdResponse(RoutineSummary summary) {
        if (summary == null) return null;
        return new GetAllRoutinesByMemberIdResponse(summary.id(), summary.name(), summary.status());
    }

    public GetAllRoutinesByTrainerIdResponse toAllRoutinesByTrainerIdResponse(RoutineSummary summary) {
        if (summary == null) return null;
        return new GetAllRoutinesByTrainerIdResponse(
                summary.id(),
                summary.name(),
                summary.status(),
                summary.memberId()
        );
    }

    public RoutineDetailResponse toRoutineDetailResponse(Routine routine, List<Exercise> catalog) {
        if (routine == null) return null;

        List<DayDetailResponse> days = routine.getDays().stream()
                .map(day -> toDayDetailResponse(day, catalog))
                .toList();

        return new RoutineDetailResponse(
                routine.getId(), routine.getName(), routine.getStartDate(), routine.getEndDate(),
                routine.getMemberId(), routine.getTrainerId(), routine.getCreatedByUserId(),
                routine.getStatus(), days
        );
    }

    private DayDetailResponse toDayDetailResponse(TrainingDay day, List<Exercise> catalog) {
        List<ExerciseItemResponse> exercises = day.getDetails().stream()
                .map(detail -> toExerciseItemResponse(detail, catalog))
                .toList();

        return new DayDetailResponse(day.getId(), day.getName(), day.getOrderNumber(), exercises);
    }

    private ExerciseItemResponse toExerciseItemResponse(RoutineDetail detail, List<Exercise> catalog) {
        Exercise catalogData = catalog.stream()
                .filter(e -> e.getId().equals(detail.getExerciseId()))
                .findFirst()
                .orElse(null);

        String name = (catalogData != null) ? catalogData.getName() : "Ejercicio Borrado";
        String img = (catalogData != null) ? catalogData.getImageUrl() : null;
        String video = (catalogData != null) ? catalogData.getVideoUrl() : null;

        return new ExerciseItemResponse(
                detail.getOrderNumber(), detail.getSets(), detail.getRepsMin(), detail.getRepsMax(),
                detail.getTargetRIR(), detail.getSuggestedWeight(), detail.getNotes(),
                detail.getExerciseId(), name, img, video
        );
    }

    public RoutineResponse toResponse(Routine routine) {
        List<RoutineResponse.TrainingDayResponse> daysResponse = routine.getDays().stream()
                .map(this::toTrainingDayResponse)
                .toList();

        return new RoutineResponse(
                routine.getId(), routine.getName(), routine.getStartDate(),
                routine.getEndDate(), routine.getMemberId(), routine.getTrainerId(), routine.getCreatedByUserId(),
                routine.getStatus(), daysResponse
        );
    }

    private RoutineResponse.TrainingDayResponse toTrainingDayResponse(TrainingDay day) {
        List<RoutineResponse.RoutineDetailResponse> detailsResponse = day.getDetails().stream()
                .map(this::toRoutineDetailResponse)
                .toList();

        return new RoutineResponse.TrainingDayResponse(
                day.getId(),
                day.getName(),
                day.getOrderNumber(),
                detailsResponse
        );
    }

    private RoutineResponse.RoutineDetailResponse toRoutineDetailResponse(RoutineDetail detail) {
        return new RoutineResponse.RoutineDetailResponse(
                detail.getId(),
                detail.getExerciseId(),
                detail.getSets(),
                detail.getRepsMin(),
                detail.getRepsMax(),
                detail.getTargetRIR(),
                detail.getSuggestedWeight(),
                detail.getNotes()
        );
    }
}