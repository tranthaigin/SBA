package com.example.employeemanagement;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "SBA301 Lab03 Employee API", version = "1.0",
        description = "In-memory Employee CRUD. Page is zero-based; size 1..100. Repeat sort=property,asc|desc for multi-sort. Default empId,asc; empId is a stable tie-breaker. V1 has four fields; V2 adds email and department. Restart resets the 30 seeded employees."))
public class EmployeeManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(EmployeeManagementApplication.class, args);
    }
}
