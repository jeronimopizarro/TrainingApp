package com.trainingapp.trainingapp.web.controller.tracker;

import com.trainingapp.trainingapp.application.useCase.tracker.FinishTrainingSessionUseCase;
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

    public TrainingSessionController(StartTrainingSessionUseCase startSessionUseCase,
                                     LogTrainingSetUseCase logSetUseCase,
                                     FinishTrainingSessionUseCase finishSessionUseCase) {
        this.startSessionUseCase = startSessionUseCase;
        this.logSetUseCase = logSetUseCase;
        this.finishSessionUseCase = finishSessionUseCase;
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

    private <T> ResponseEntity<T> buildCreatedResponse(String path, T body) {
        URI location = URI.create(path);
        return ResponseEntity.created(location).body(body);
    }
}