package com.trainingapp.trainingapp.domain.entity.Access;

import com.trainingapp.trainingapp.domain.exception.access.InvalidAccessLogException;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AccessLog {

    private final Long id;
    private Long memberId;
    private Long gymId;
    private LocalDateTime timestamp;
    private boolean accessGranted;
    private String message;

    public AccessLog(Long id, Long memberId, Long gymId, LocalDateTime timestamp, boolean accessGranted, String message) {
        this.id = id;
        this.memberId = memberId;
        this.gymId = gymId;
        this.timestamp = timestamp;
        this.accessGranted = accessGranted;
        this.message = message;
        validate();
    }

    private void validate() {
        if (this.memberId == null) {
            throw new InvalidAccessLogException("El registro de acceso debe estar asociado a un socio.");
        }
        if (this.gymId == null) {
            throw new InvalidAccessLogException("El registro de acceso debe indicar en qué gimnasio ocurrió.");
        }
        if (this.timestamp == null) {
            throw new InvalidAccessLogException("El registro de acceso debe tener una fecha y hora exacta.");
        }
    }

    public static AccessLog createNew(Long memberId, Long gymId, boolean accessGranted, String message) {
        return new AccessLog(null, memberId, gymId, LocalDateTime.now(), accessGranted, message);
    }

    public static AccessLog restore(Long id, Long memberId, Long gymId, LocalDateTime timestamp,
                                    boolean accessGranted, String message) {
        return new AccessLog(id, memberId, gymId, timestamp, accessGranted, message);
    }
}