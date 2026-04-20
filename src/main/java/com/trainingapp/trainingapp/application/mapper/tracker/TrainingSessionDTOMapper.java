package com.trainingapp.trainingapp.application.mapper.tracker;

import com.trainingapp.trainingapp.domain.entity.tracker.SetLog;
import com.trainingapp.trainingapp.domain.entity.tracker.TrainingSession;
import com.trainingapp.trainingapp.web.dto.tracker.SessionResponse;
import com.trainingapp.trainingapp.web.dto.tracker.SetLogResponse;
import com.trainingapp.trainingapp.web.dto.tracker.StartSessionRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TrainingSessionDTOMapper {

    public TrainingSession toDomainStartSession(StartSessionRequest request, Long memberId, Long gymId) {
        if (request == null) return null;

        // routineId puede venir null desde el request si es entrenamiento libre
        return TrainingSession.startNew(
                memberId,
                request.routineId(),
                request.trainingDayId(),
                gymId);
    }

    public SessionResponse toResponse(TrainingSession session) {
        List<SetLogResponse> loggedSets = session.getSets() != null
            ? session.getSets().stream().map(this::toSetLogResponse).toList()
            : new java.util.ArrayList<>();

        return new SessionResponse(
                session.getId(),
                session.getMemberId(),
                session.getRoutineId(),
                session.getTrainingDayId(),
                session.getStartTime(),
                session.getEndTime(),
                session.getStatus(),
                loggedSets
        );
    }

    public SetLogResponse toSetLogResponse(SetLog setLog) {
        return new SetLogResponse(
                setLog.getId(),
                setLog.getExerciseId(),
                setLog.getSetNumber(),
                setLog.getRepsPerformed(),
                setLog.getWeightLifted(),
                setLog.getRir(),
                setLog.getNotes()
        );
    }
}