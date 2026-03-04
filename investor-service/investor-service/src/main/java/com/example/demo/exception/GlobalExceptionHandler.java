package com.example.demo.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)

    public Map<String, String> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
          .getFieldErrors()
          .forEach(error -> {

              errors.put(
                error.getField(),
                error.getDefaultMessage()
              );

          });

        return errors;
    }



    @ExceptionHandler(InvalidDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)

    public Map<String, Object> handleInvalidData(
            InvalidDataException ex,
            HttpServletRequest request) {

        Map<String, Object> error = new HashMap<>();

        error.put("time", LocalDateTime.now());
        error.put("status", 400);
        error.put("error", ex.getMessage());
        error.put("path", request.getRequestURI());

        return error;
    }



    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)

    public Map<String, Object> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        Map<String, Object> error = new HashMap<>();

        error.put("time", LocalDateTime.now());
        error.put("status", 404);
        error.put("error", ex.getMessage());
        error.put("path", request.getRequestURI());

        return error;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        Map<String, Object> error = new HashMap<>();
        error.put("time", LocalDateTime.now());
        error.put("status", 500);
        error.put("error", ex.getMessage());
        error.put("path", request.getRequestURI());
        return error;
    }

}
