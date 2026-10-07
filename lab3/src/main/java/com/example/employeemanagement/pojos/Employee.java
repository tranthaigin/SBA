package com.example.employeemanagement.pojos;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

// Immutable values prevent callers from mutating repository state without an update.
public record Employee(
        @NotBlank @Pattern(regexp = "E[0-9]{3,6}") String empId,
        @NotBlank @Size(max = 100) String empName,
        @NotBlank @Size(max = 100) String designation,
        @NotNull @DecimalMin("0.0") @Digits(integer = 12, fraction = 2) BigDecimal salary,
        @Email @Size(max = 150) String email,
        @Size(max = 100) String department) {
    public Employee(String empId, String empName, String designation, BigDecimal salary) {
        this(empId, empName, designation, salary, null, null);
    }
}
