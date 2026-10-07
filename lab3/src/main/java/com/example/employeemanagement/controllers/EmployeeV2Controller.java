package com.example.employeemanagement.controllers;

import com.example.employeemanagement.dto.*;
import com.example.employeemanagement.pojos.Employee;
import com.example.employeemanagement.services.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v2/employees", produces = "application/json")
public class EmployeeV2Controller {
    private final IEmployeeService service;
    public EmployeeV2Controller(IEmployeeService service) { this.service = service; }

    @GetMapping
    @Operation(summary = "V2 Page adds email and department to employee representations")
    public PageResponse<Employee> getAll(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String sort,
            HttpServletRequest request) {
        return PageResponse.from(service.getAllEmployees(PagingPolicy.create(page, size, request.getParameterValues("sort"))));
    }

    @GetMapping("/slice")
    @Operation(summary = "V2 Slice adds email and department; no total count")
    public SliceResponse<Employee> getSlice(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String sort,
            HttpServletRequest request) {
        return SliceResponse.from(service.getEmployeeSlice(PagingPolicy.create(page, size, request.getParameterValues("sort"))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get V2 employee, including email and department")
    public Employee getById(@PathVariable String id) { return service.getEmployeeById(id); }
}
