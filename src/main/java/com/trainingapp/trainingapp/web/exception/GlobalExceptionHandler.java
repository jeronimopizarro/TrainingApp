package com.trainingapp.trainingapp.web.exception;

import com.trainingapp.trainingapp.domain.exception.access.UnauthorizedQrGenerationException;
import com.trainingapp.trainingapp.domain.exception.auth.UnauthenticatedUserException;
import com.trainingapp.trainingapp.domain.exception.exercise.*;
import com.trainingapp.trainingapp.domain.exception.gym.DuplicateGymNameException;
import com.trainingapp.trainingapp.domain.exception.gym.GymAlreadyExistsException;
import com.trainingapp.trainingapp.domain.exception.gym.GymNotFoundException;
import com.trainingapp.trainingapp.domain.exception.gym.UnauthorizedGymAccessException;
import com.trainingapp.trainingapp.domain.exception.membership.DuplicateMembershipPlanNameException;
import com.trainingapp.trainingapp.domain.exception.membership.InactiveMembershipPlanException;
import com.trainingapp.trainingapp.domain.exception.membership.MembershipNotFoundException;
import com.trainingapp.trainingapp.domain.exception.membership.MembershipPlanAccessDeniedException;
import com.trainingapp.trainingapp.domain.exception.product.ProductNotFoundException;
import com.trainingapp.trainingapp.domain.exception.routine.*;
import com.trainingapp.trainingapp.domain.exception.subscription.*;
import com.trainingapp.trainingapp.domain.exception.tracker.*;
import com.trainingapp.trainingapp.domain.exception.user.*;
import com.trainingapp.trainingapp.domain.exception.user.member.MemberAccessDeniedException;
import com.trainingapp.trainingapp.domain.exception.user.member.MemberNotFoundException;
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

    // =================================================================================
    // 1. 404 NOT FOUND - Recursos no encontrados
    // =================================================================================
    @ExceptionHandler({
            RoutineNotFoundException.class,
            MuscleGroupNotFoundException.class,
            ExerciseNotFoundException.class,
            GymNotFoundException.class,
            TrainerNotFoundException.class,
            MemberNotFoundException.class,
            AdminNotFoundException.class,
            MembershipNotFoundException.class,
            ActiveSubscriptionNotFoundException.class,
            ProductNotFoundException.class,
            SubscriptionNotFoundException.class,
            TrainingSessionNotFoundException.class
    })
    public ResponseEntity<ApiErrorResponse> handleNotFoundExceptions(RuntimeException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Not Found", ex);
    }

    // =================================================================================
    // 2. 403 FORBIDDEN - Problemas de permisos, roles y cruce de gimnasios
    // =================================================================================
    @ExceptionHandler({
            MemberAccessDeniedException.class,
            MembershipPlanAccessDeniedException.class,
            UnauthorizedQrGenerationException.class,
            UnauthorizedExerciseAccessException.class,
            UnauthorizedBaseExerciseModificationException.class,
            UnauthorizedExerciseModificationException.class,
            UnauthorizedGymAccessException.class,
            UnauthorizedRoutineAccessException.class,
            UnauthorizedRoutineModificationException.class,
            UnauthorizedProfileAccessException.class,
            UnauthorizedProfileModificationException.class,
            UnauthorizedSessionAccessException.class,
            TrainingRequiresActiveSubscriptionException.class
    })
    public ResponseEntity<ApiErrorResponse> handleForbiddenExceptions(RuntimeException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "Forbidden - Access Denied", ex);
    }

    // =================================================================================
    // 3. 401 UNAUTHORIZED - Problemas de sesión o token
    // =================================================================================
    @ExceptionHandler(UnauthenticatedUserException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthenticated(UnauthenticatedUserException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", ex);
    }

    // =================================================================================
    // 4. 409 CONFLICT - Duplicados, transiciones de estado inválidas o conflictos lógicos
    // =================================================================================
    @ExceptionHandler({
            ActiveSubscriptionAlreadyExistsException.class,
            SubscriptionAlreadyCancelledException.class,
            SubscriptionAlreadyExpiredException.class,
            ActiveRoutineAlreadyExistsException.class,
            ActiveRoutineRequestAlreadyExistsException.class,
            ActiveSessionAlreadyExistsException.class,
            EmailAlreadyExistsException.class,
            DuplicateGymNameException.class,
            GymAlreadyExistsException.class,
            GymExerciseAlreadyExistsException.class,
            BaseExerciseAlreadyExistsException.class,
            DuplicateMembershipPlanNameException.class,
            ExerciseAlreadyActiveException.class,
            ExerciseAlreadyInactiveException.class,
            InvalidRoutineRequestStateException.class,
            InvalidSessionStateException.class,
            UserAlreadyActiveException.class,
            UserAlreadyInactiveException.class
    })
    public ResponseEntity<ApiErrorResponse> handleConflictExceptions(RuntimeException ex) {
        return buildResponse(HttpStatus.CONFLICT, "Conflict - Business Rule Violation", ex);
    }

    // =================================================================================
    // 5. 400 BAD REQUEST - Fechas inválidas, estados incorrectos o argumentos faltantes
    // =================================================================================
    @ExceptionHandler({
            InvalidRoutineStateException.class,
            InvalidSubscriptionStartDateException.class,
            InactiveMembershipPlanException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ApiErrorResponse> handleBadRequestExceptions(RuntimeException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request - Invalid Business Rule", ex);
    }

    // =================================================================================
    // 6. CATCH-ALL PARA ILLEGAL STATE (Por si quedó algún string quemado en el código)
    // =================================================================================
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalState(IllegalStateException ex) {
        return buildResponse(HttpStatus.CONFLICT, "Conflict - Illegal State", ex);
    }

    // =================================================================================
    // 7. VALIDACIONES DE SPRING (@Valid) - Errores de DTOs
    // =================================================================================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String errorTitle, RuntimeException ex) {
        ApiErrorResponse errorDetails = new ApiErrorResponse(
                status.value(),
                errorTitle,
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(status).body(errorDetails);
    }
}