package com.example.events.api;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;

public final class Contracts {
    private Contracts() {}
    public record Signup(
        @NotBlank @Pattern(regexp="[a-zA-Z0-9_]{3,32}", message="用户名须为3至32位字母、数字或下划线") String username,
        @NotBlank @Size(min=8, max=64) String password) {}
    public record EventInput(
        @NotBlank @Size(max=120) String title,
        @NotBlank @Size(max=10000) String description,
        @NotBlank @Size(max=255) String location,
        @NotNull @Future Instant eventTime,
        @NotNull @Min(1) @Max(1000000) Integer capacity) {}
    public record UserView(Long id, String username) {}
    public record EventView(Long id, String title, String description, String location,
        Instant eventTime, int capacity, int registeredCount, boolean cancelled,
        Long organizerId, String organizerName, Instant createdAt, Instant updatedAt) implements Serializable {}
    public record PageView<T>(List<T> content, int page, int size, long totalElements, int totalPages) implements Serializable {}
    public record RegistrationView(Long id, Long eventId, Long userId, Instant createdAt) {}
}
