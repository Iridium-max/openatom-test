package com.example.task10.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthResponse(String message, String token, Long expiresIn) {
    public static AuthResponse message(String message) {
        return new AuthResponse(message, null, null);
    }
}