package com.example.employeemanagement.controllers;

import com.example.employeemanagement.dto.*;
import com.example.employeemanagement.pojos.Employee;
import com.example.employeemanagement.services.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = {"/api/employees", "/api/v1/employees"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class EmployeeController {
    private final IEmployeeService service;
    public EmployeeController(IEmployeeService service) { this.service = service; }

    @GetMapping
    @Operation(summary = "List employees with Page metadata (V1)")
    public PageResponse<EmployeeV1> getAll(
            @RequestParam(defaultValue = "0") @Parameter(schema = @Schema(type = "integer", minimum = "0", defaultValue = "0")) int page,
            @RequestParam(defaultValue = "10") @Parameter(schema = @Schema(type = "integer", minimum = "1", maximum = "100", defaultValue = "10")) int size,
            @RequestParam(required = false) @Parameter(description = "Repeat sort=empName,asc&sort=salary,desc. Default empId,asc.") String sort,
            HttpServletRequest request) {
        return PageResponse.from(service.getAllEmployees(PagingPolicy.create(page, size, request.getParameterValues("sort")))
                .map(EmployeeV1::from));
    }

    @GetMapping("/slice")
    @Operation(summary = "List employees with Slice metadata, without totals (V1)")
    public SliceResponse<EmployeeV1> getSlice(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String sort,
            HttpServletRequest request) {
        return SliceResponse.from(service.getEmployeeSlice(PagingPolicy.create(page, size, request.getParameterValues("sort")))
                .map(EmployeeV1::from));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employee by empId")
    @ApiResponse(responseCode = "404", description = "Employee not found")
    public EmployeeV1 getById(@PathVariable String id) { return EmployeeV1.from(service.getEmployeeById(id)); }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create employee; duplicate empId returns 409")
    @ApiResponse(responseCode = "201", description = "Created; Location identifies the new employee")
    @ApiResponse(responseCode = "400", description = "Invalid employee")
    @ApiResponse(responseCode = "409", description = "Duplicate empId")
    public ResponseEntity<EmployeeV1> create(@Valid @RequestBody Employee employee, HttpServletRequest request) {
        Employee saved = service.createEmployee(employee);
        return ResponseEntity.created(URI.create(request.getRequestURI() + "/" + saved.empId())).body(EmployeeV1.from(saved));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Replace employee; path/body empId must agree")
    public EmployeeV1 update(@PathVariable String id, @Valid @RequestBody Employee employee) {
        return EmployeeV1.from(service.updateEmployee(id, employee));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete employee")
    @ApiResponse(responseCode = "204", description = "Deleted; empty body")
    @ApiResponse(responseCode = "404", description = "Employee not found")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
