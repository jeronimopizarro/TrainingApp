package com.trainingapp.trainingapp.domain.exception.membership;

public class MembershipPlanAccessDeniedException extends RuntimeException {
    public MembershipPlanAccessDeniedException(String message) {
        super(message);
    }
}