package com.example.employeemanagement.repositories;

import com.example.employeemanagement.pojos.Employee;
import org.springframework.data.domain.*;

public interface IEmployeeRepository {
    Page<Employee> getAllEmployees(Pageable pageable);
    Slice<Employee> getEmployeeSlice(Pageable pageable);
    Employee getEmployeeById(String id);
    Employee createEmployee(Employee employee);
    Employee updateEmployee(String id, Employee employee);
    void deleteEmployee(String id);
}
