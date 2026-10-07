package com.example.employeemanagement.exceptions;

import org.springframework.http.HttpStatus;

public class EmployeeNotFoundException extends ApiException {
    public EmployeeNotFoundException(String id) {
        super(HttpStatus.NOT_FOUND, "Employee " + id + " was not found");
    }
}
