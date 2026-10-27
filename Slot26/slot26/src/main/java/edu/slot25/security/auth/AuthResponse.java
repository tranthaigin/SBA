package edu.slot25.security.auth;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}
