package edu.slot25.security.user;

public record UserResponse(
        Long id,
        String email,
        String role
) {
}
