package com.trainingapp.trainingapp.web.exception;

import com.trainingapp.trainingapp.domain.exception.RoutineNotFoundException;
import com.trainingapp.trainingapp.web.dto.ApiErrorResponse;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        // Creamos un diccionario vacío para guardar los errores
        Map<String, String> errors = new HashMap<>();

        // Extraemos todos los errores que encontró el patovica y los guardamos
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        // Devolvemos un 400 Bad Request con la lista de errores
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
