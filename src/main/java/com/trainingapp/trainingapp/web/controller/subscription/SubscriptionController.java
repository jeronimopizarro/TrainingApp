package com.trainingapp.trainingapp.web.controller.subscription;

import com.trainingapp.trainingapp.application.useCase.subscription.CancelSubscriptionUseCase;
import com.trainingapp.trainingapp.application.useCase.subscription.CreateSubscriptionUseCase;
import com.trainingapp.trainingapp.application.useCase.subscription.GetActiveSubscriptionByMemberUseCase;
import com.trainingapp.trainingapp.application.useCase.subscription.GetAllSubscriptionsByMemberUseCase;
import com.trainingapp.trainingapp.web.dto.subscription.CreateSubscriptionRequest;
import com.trainingapp.trainingapp.web.dto.subscription.SubscriptionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private final CreateSubscriptionUseCase createSubscriptionUseCase;
    private final GetActiveSubscriptionByMemberUseCase getActiveSubscriptionByMemberUseCase;
    private final GetAllSubscriptionsByMemberUseCase getAllSubscriptionsByMemberUseCase;
    private final CancelSubscriptionUseCase cancelSubscriptionUseCase;

    public SubscriptionController(CreateSubscriptionUseCase createSubscriptionUseCase,
                                  GetActiveSubscriptionByMemberUseCase getActiveSubscriptionByMemberUseCase,
                                  GetAllSubscriptionsByMemberUseCase getAllSubscriptionsByMemberUseCase,
                                  CancelSubscriptionUseCase cancelSubscriptionUseCase) {
        this.createSubscriptionUseCase = createSubscriptionUseCase;
        this.getActiveSubscriptionByMemberUseCase = getActiveSubscriptionByMemberUseCase;
        this.getAllSubscriptionsByMemberUseCase = getAllSubscriptionsByMemberUseCase;
        this.cancelSubscriptionUseCase = cancelSubscriptionUseCase;
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN')")
    @PostMapping
    public ResponseEntity<SubscriptionResponse> createSubscription(
            @Valid @RequestBody CreateSubscriptionRequest request) {
        SubscriptionResponse response = createSubscriptionUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER', 'MEMBER')")
    @GetMapping("/active")
    public ResponseEntity<SubscriptionResponse> getActiveSubscriptionByMember(
            @RequestParam Long memberId) {
        SubscriptionResponse response = getActiveSubscriptionByMemberUseCase.execute(memberId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER', 'MEMBER')")
    @GetMapping()
    public ResponseEntity<List<SubscriptionResponse>> getAllSubscriptionsByMember(
            @RequestParam Long memberId) {
        List<SubscriptionResponse> response = getAllSubscriptionsByMemberUseCase.execute(memberId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN')")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<SubscriptionResponse> cancelSubscription(
            @PathVariable Long id) {
        SubscriptionResponse response = cancelSubscriptionUseCase.execute(id);
        return ResponseEntity.ok(response);
    }
}