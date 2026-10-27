package com.example.lab06jwt.controllers;

import com.example.lab06jwt.entities.User;
import com.example.lab06jwt.responses.UserResponse;
import com.example.lab06jwt.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> authenticatedUser(Authentication authentication) {
        return ResponseEntity.ok(UserResponse.from((User) authentication.getPrincipal()));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> allUsers() {
        return ResponseEntity.ok(userService.allUsers().stream().map(UserResponse::from).toList());
    }
}
