package com.example.task10.controller;

import com.example.task10.dto.AuthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<AuthResponse> handle(ResponseStatusException e) {
        return ResponseEntity.status(e.getStatusCode()).body(AuthResponse.message(e.getReason()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<AuthResponse> handleBadJson(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(AuthResponse.message("请求体必须是正确的JSON"));
    }
}