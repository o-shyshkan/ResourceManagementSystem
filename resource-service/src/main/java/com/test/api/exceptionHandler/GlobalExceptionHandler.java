package com.test.api.exceptionHandler;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    public static final String KEY_ERRORS = "errors";
    public static final String AN_UNEXPECTED_ERROR_OCCURRED = "An unexpected error occurred";
    public static final String INVALID_VALUE_FOR_PARAMETER_S_S = "Invalid value for parameter '%s': '%s'";

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<?> generateNotValidException(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        ex.getAllErrors().forEach(err -> errors.add(err.getDefaultMessage()));
        Map<String, List<String>> result = new HashMap<>();
        result.put(KEY_ERRORS, errors);
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<?> generateConstraintViolationException(ConstraintViolationException ex) {
        List<String> errors = new ArrayList<>();
        ex.getConstraintViolations().forEach(err -> errors.add(err.getMessage()));
        Map<String, List<String>> result = new HashMap<>();
        result.put(KEY_ERRORS, errors);
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, List<String>>> handleEntityNotFoundException(EntityNotFoundException ex) {
        String errorMessage = ex.getMessage() != null ? ex.getMessage() : AN_UNEXPECTED_ERROR_OCCURRED;
        List<String> errors = List.of(errorMessage);
        Map<String, List<String>> result = Map.of(KEY_ERRORS, errors);
        return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, List<String>>> handleRuntimeException(RuntimeException ex) {
        String errorMessage = ex.getMessage() != null ? ex.getMessage() : AN_UNEXPECTED_ERROR_OCCURRED;
        List<String> errors = List.of(errorMessage);
        Map<String, List<String>> result = Map.of(KEY_ERRORS, errors);
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, List<String>>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String error = String.format(INVALID_VALUE_FOR_PARAMETER_S_S, ex.getName(), ex.getValue());
        Map<String, List<String>> result = Map.of(KEY_ERRORS, List.of(error));
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }
}
