package com.trainingapp.trainingapp.domain.exception.membership;

public class DuplicateMembershipPlanNameException extends RuntimeException {
    public DuplicateMembershipPlanNameException(String message) {
        super(message);
    }
}
