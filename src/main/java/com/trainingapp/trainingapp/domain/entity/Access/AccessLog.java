package com.trainingapp.trainingapp.domain.entity.Access;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AccessLog {

    private Long id;
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
    }

    public AccessLog(Long memberId, Long gymId, LocalDateTime timestamp, boolean accessGranted, String message) {
        this.memberId = memberId;
        this.gymId = gymId;
        this.timestamp = timestamp;
        this.accessGranted = accessGranted;
        this.message = message;
    }
}