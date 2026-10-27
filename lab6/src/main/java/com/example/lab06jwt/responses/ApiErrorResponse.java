package com.example.lab06jwt.responses;

import java.time.Instant;
import java.util.Map;

public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String path,
        Map<String, String> fields
) {
    public static ApiErrorResponse of(int status, String error, String path) {
        return new ApiErrorResponse(Instant.now(), status, error, path, null);
    }
}
