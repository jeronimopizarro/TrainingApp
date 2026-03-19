package com.trainingapp.trainingapp.web.exception;

import com.trainingapp.trainingapp.domain.exception.exercise.ExerciseNotFoundException;
import com.trainingapp.trainingapp.domain.exception.exercise.MuscleGroupNotFoundException;
import com.trainingapp.trainingapp.domain.exception.gym.GymNotFoundException;
import com.trainingapp.trainingapp.domain.exception.membership.MembershipNotFoundException;
import com.trainingapp.trainingapp.domain.exception.routine.RoutineNotFoundException;
import com.trainingapp.trainingapp.domain.exception.subscription.ActiveSubscriptionAlreadyExistsException;
import com.trainingapp.trainingapp.domain.exception.subscription.ActiveSubscriptionNotFoundException;
import com.trainingapp.trainingapp.domain.exception.user.AdminNotFoundException;
import com.trainingapp.trainingapp.domain.exception.user.MemberAccessDeniedException;
import com.trainingapp.trainingapp.domain.exception.user.MemberNotFoundException;
import com.trainingapp.trainingapp.domain.exception.user.TrainerNotFoundException;
import com.trainingapp.trainingapp.web.dto.routine.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            RoutineNotFoundException.class,
            MuscleGroupNotFoundException.class,
            ExerciseNotFoundException.class,
            GymNotFoundException.class,
            TrainerNotFoundException.class,
            MemberNotFoundException.class,
            AdminNotFoundException.class,
            MembershipNotFoundException.class,
            ActiveSubscriptionNotFoundException.class})
    public ResponseEntity<ApiErrorResponse> handleNotFoundExceptions(RuntimeException ex) {

        ApiErrorResponse errorDetails = new ApiErrorResponse(HttpStatus.NOT_FOUND.value(), // 404
                "Not Found", ex.getMessage(), LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDetails);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        // Extraemos todos los errores
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalState(IllegalStateException ex) {

        ApiErrorResponse errorDetails = new ApiErrorResponse(HttpStatus.CONFLICT.value(),
                "Conflict - Business Rule Violation", ex.getMessage(), LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDetails);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {

        ApiErrorResponse errorDetails = new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Bad Request - Invalid Argument", ex.getMessage(), LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(ActiveSubscriptionAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleSubscriptionConflict(ActiveSubscriptionAlreadyExistsException ex) {

        ApiErrorResponse errorDetails = new ApiErrorResponse(HttpStatus.CONFLICT.value(), // 409
                "Conflict - Active Subscription Exists", ex.getMessage(), LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDetails);
    }

    // Atrapa cuando falla el MemberAccessValidator (ej: un admin queriendo ver un socio de otro gym)
    @ExceptionHandler(MemberAccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(MemberAccessDeniedException ex) {

        ApiErrorResponse errorDetails = new ApiErrorResponse(HttpStatus.FORBIDDEN.value(), // 403
                "Forbidden - Access Denied", ex.getMessage(), LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDetails);
    }
}