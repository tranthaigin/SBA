package com.example.lab06jwt.services;

import com.example.lab06jwt.dtos.LoginUserDto;
import com.example.lab06jwt.dtos.RegisterUserDto;
import com.example.lab06jwt.entities.User;
import com.example.lab06jwt.exceptions.DuplicateEmailException;
import com.example.lab06jwt.repositories.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                 AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public User signup(RegisterUserDto input) {
        String email = normalizeEmail(input.email());
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateEmailException();
        }

        User user = new User();
        user.setFullName(input.fullName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(input.password()));
        try {
            return userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateEmailException();
        }
    }

    public User authenticate(LoginUserDto input) {
        String email = normalizeEmail(input.email());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, input.password()));
        return userRepository.findByEmailIgnoreCase(email).orElseThrow();
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
