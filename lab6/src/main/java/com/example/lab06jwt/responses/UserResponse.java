package com.example.lab06jwt.responses;

import com.example.lab06jwt.entities.User;

public record UserResponse(Integer id, String email, String fullName) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getFullName());
    }
}
