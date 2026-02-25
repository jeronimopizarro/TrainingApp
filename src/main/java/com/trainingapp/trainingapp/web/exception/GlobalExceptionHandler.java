package com.trainingapp.trainingapp.web.exception;

import com.trainingapp.trainingapp.domain.exception.RoutineNotFoundException;
import com.trainingapp.trainingapp.web.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(RoutineNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleRoutineNotFound(RoutineNotFoundException ex) {

        // Armamos el JSON prolijo
        ApiErrorResponse errorDetails = new ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(), // 404
                "Not Found",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDetails);
    }
}
