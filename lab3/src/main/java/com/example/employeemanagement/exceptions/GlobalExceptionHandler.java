package com.example.employeemanagement.exceptions;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Object> handleApi(ApiException ex, WebRequest request) {
        return response(ex.status(), ex.getMessage(), Map.of(), new HttpHeaders(), request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> fields = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> fields.putIfAbsent(e.getField(), e.getDefaultMessage()));
        return response(status, "Employee validation failed", fields, headers, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String message = switch (status.value()) {
            case 400 -> "Invalid request body or parameter";
            case 404 -> "Resource was not found";
            case 405 -> "HTTP method is not supported";
            case 406 -> "Requested response media type is not supported";
            case 415 -> "Use Content-Type application/json";
            default -> "Request could not be processed";
        };
        return response(status, message, Map.of(), headers, request);
    }

    private ResponseEntity<Object> response(HttpStatusCode status, String message, Map<String, String> fields,
            HttpHeaders headers, WebRequest request) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        HttpStatus known = HttpStatus.resolve(status.value());
        return new ResponseEntity<>(new ApiError(Instant.now(), status.value(),
                known == null ? "Error" : known.getReasonPhrase(), message, path, fields), headers, status);
    }
}
