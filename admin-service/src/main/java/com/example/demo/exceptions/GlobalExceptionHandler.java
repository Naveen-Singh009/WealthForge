package com.example.demo.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StockNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStockNotFound(
            StockNotFoundException ex,
            HttpServletRequest request) {

        return buildResponse(ex, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InsufficientStockQuantityException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStock(
            InsufficientStockQuantityException ex,
            HttpServletRequest request) {

        return buildResponse(ex, request, HttpStatus.BAD_REQUEST);
    }

    
    @ExceptionHandler(CompanyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCompanyNotFound(
            CompanyNotFoundException ex,
            HttpServletRequest request) {

        return buildResponse(ex, request, HttpStatus.NOT_FOUND);
    }

    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(
            RuntimeException ex,
            HttpServletRequest request) {

        return buildResponse(ex, request, HttpStatus.BAD_REQUEST);
    }

  
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobal(
            Exception ex,
            HttpServletRequest request) {

        return buildResponse(ex, request,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

  
    private ResponseEntity<ErrorResponse> buildResponse(
            Exception ex,
            HttpServletRequest request,
            HttpStatus status) {

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(error, status);
    }
}