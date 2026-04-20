package com.trainingapp.trainingapp.web.controller.tracker;

import com.trainingapp.trainingapp.application.useCase.tracker.CancelTrainingSessionUseCase;
import com.trainingapp.trainingapp.application.useCase.tracker.FinishTrainingSessionUseCase;
import com.trainingapp.trainingapp.application.useCase.tracker.GetActiveTrainingSessionUseCase;
import com.trainingapp.trainingapp.application.useCase.tracker.LogTrainingSetUseCase;
import com.trainingapp.trainingapp.application.useCase.tracker.StartTrainingSessionUseCase;
import com.trainingapp.trainingapp.web.dto.tracker.LogSetRequest;
import com.trainingapp.trainingapp.web.dto.tracker.SessionResponse;
import com.trainingapp.trainingapp.web.dto.tracker.SetLogResponse;
import com.trainingapp.trainingapp.web.dto.tracker.StartSessionRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/sessions")
public class TrainingSessionController {

    private final StartTrainingSessionUseCase startSessionUseCase;
    private final LogTrainingSetUseCase logSetUseCase;
    private final FinishTrainingSessionUseCase finishSessionUseCase;
    private final GetActiveTrainingSessionUseCase getActiveSessionUseCase;
    private final CancelTrainingSessionUseCase cancelSessionUseCase;

    public TrainingSessionController(StartTrainingSessionUseCase startSessionUseCase,
                                     LogTrainingSetUseCase logSetUseCase,
                                     FinishTrainingSessionUseCase finishSessionUseCase,
                                     GetActiveTrainingSessionUseCase getActiveSessionUseCase,
                                     CancelTrainingSessionUseCase cancelSessionUseCase) {
        this.startSessionUseCase = startSessionUseCase;
        this.logSetUseCase = logSetUseCase;
        this.finishSessionUseCase = finishSessionUseCase;
        this.getActiveSessionUseCase = getActiveSessionUseCase;
        this.cancelSessionUseCase = cancelSessionUseCase;
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<SessionResponse> getActiveSession() {
        SessionResponse response = getActiveSessionUseCase.execute();
        return response != null ? ResponseEntity.ok(response) : ResponseEntity.noContent().build();
    }

    @PostMapping("/start")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<SessionResponse> startSession(@Valid @RequestBody StartSessionRequest request) {
        SessionResponse response = startSessionUseCase.execute(request);
        return buildCreatedResponse("/sessions/" + response.id(), response);
    }

    @PostMapping("/{sessionId}/sets")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<SetLogResponse> logSet(@PathVariable Long sessionId,
                                                 @Valid @RequestBody LogSetRequest request) {
        SetLogResponse response = logSetUseCase.execute(sessionId, request);
        return buildCreatedResponse("/sessions/" + sessionId + "/sets/" + response.id(), response);
    }

    @PatchMapping("/{sessionId}/finish")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<SessionResponse> finishSession(@PathVariable Long sessionId) {
        SessionResponse response = finishSessionUseCase.execute(sessionId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{sessionId}/cancel")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<SessionResponse> cancelSession(@PathVariable Long sessionId) {
        SessionResponse response = cancelSessionUseCase.execute(sessionId);
        return ResponseEntity.ok(response);
    }

    private <T> ResponseEntity<T> buildCreatedResponse(String path, T body) {
        URI location = URI.create(path);
        return ResponseEntity.created(location).body(body);
    }
}
