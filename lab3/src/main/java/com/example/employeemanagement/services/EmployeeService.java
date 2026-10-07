package com.example.employeemanagement.services;

import com.example.employeemanagement.exceptions.*;
import com.example.employeemanagement.pojos.Employee;
import com.example.employeemanagement.repositories.IEmployeeRepository;
import jakarta.validation.Validator;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService implements IEmployeeService {
    private final IEmployeeRepository repository;
    private final Validator validator;

    public EmployeeService(IEmployeeRepository repository, Validator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    @Override public Page<Employee> getAllEmployees(Pageable pageable) { return repository.getAllEmployees(pageable); }
    @Override public Slice<Employee> getEmployeeSlice(Pageable pageable) { return repository.getEmployeeSlice(pageable); }

    @Override
    public Employee getEmployeeById(String id) {
        Employee employee = repository.getEmployeeById(id);
        if (employee == null) { throw new EmployeeNotFoundException(id); }
        return employee;
    }

    @Override
    public Employee createEmployee(Employee employee) {
        validate(employee);
        return repository.createEmployee(employee);
    }

    @Override
    public Employee updateEmployee(String id, Employee employee) {
        validate(employee);
        if (!id.equals(employee.empId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Path empId must match body empId");
        }
        return repository.updateEmployee(id, employee);
    }

    @Override public void deleteEmployee(String id) { repository.deleteEmployee(id); }

    private void validate(Employee employee) {
        if (employee == null || !validator.validate(employee).isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Employee validation failed");
        }
    }
}
