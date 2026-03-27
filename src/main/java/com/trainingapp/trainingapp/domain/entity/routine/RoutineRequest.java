package com.trainingapp.trainingapp.domain.entity.routine;

import com.trainingapp.trainingapp.domain.enums.routine.RoutineRequestStatus;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class RoutineRequest {

    private final Long id;
    private final Long memberId;
    private final Long gymId;
    private final String note;
    private final LocalDate requestDate;
    private RoutineRequestStatus status;

    public RoutineRequest(Long id, Long memberId, Long gymId, String note, LocalDate requestDate,
                          RoutineRequestStatus status) {
        validateRoutineRequest(memberId, gymId, requestDate, status);

        this.id = id;
        this.memberId = memberId;
        this.gymId = gymId;
        this.note = note;
        this.requestDate = requestDate;
        this.status = status;
    }

    private void validateRoutineRequest(Long memberId, Long gymId, LocalDate requestDate,
                                        RoutineRequestStatus status) {
        if (memberId == null) {
            throw new IllegalArgumentException("The request must be associated with a member.");
        }
        if (gymId == null) {
            throw new IllegalArgumentException("The request must be associated with a gym.");
        }
        if (requestDate == null) {
            throw new IllegalArgumentException("The request date is mandatory.");
        }
        if (status == null) {
            throw new IllegalArgumentException("The request status is mandatory.");
        }
    }

    public static RoutineRequest createNew(Long memberId, Long gymId, String note) {
        return new RoutineRequest(null, memberId, gymId, note, LocalDate.now(),
                RoutineRequestStatus.PENDING
        );
    }

    public void complete() {
        if (this.status == RoutineRequestStatus.COMPLETED) {
            throw new IllegalStateException("This routine request is already completed.");
        }
        this.status = RoutineRequestStatus.COMPLETED;
    }

    public void cancel() {
        if (this.status != RoutineRequestStatus.PENDING) {
            throw new IllegalStateException("Solo se pueden cancelar solicitudes pendientes.");
        }
        this.status = RoutineRequestStatus.CANCELLED;
    }
}