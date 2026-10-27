package com.example.lab06jwt.services;

import com.example.lab06jwt.entities.User;
import com.example.lab06jwt.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public List<User> allUsers() {
        List<User> users = new ArrayList<>();
        repository.findAll().forEach(users::add);
        return users;
    }
}
