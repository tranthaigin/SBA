package com.example.lab06jwt.controllers;

import com.example.lab06jwt.dtos.LoginUserDto;
import com.example.lab06jwt.dtos.RegisterUserDto;
import com.example.lab06jwt.entities.User;
import com.example.lab06jwt.responses.LoginResponse;
import com.example.lab06jwt.responses.UserResponse;
import com.example.lab06jwt.services.AuthenticationService;
import com.example.lab06jwt.services.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterUserDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserResponse.from(authenticationService.signup(dto)));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginUserDto dto) {
        User authenticated = authenticationService.authenticate(dto);
        String jwt = jwtService.generateToken(authenticated);
        return ResponseEntity.ok(new LoginResponse(jwt, jwtService.getExpirationTime()));
    }
}
