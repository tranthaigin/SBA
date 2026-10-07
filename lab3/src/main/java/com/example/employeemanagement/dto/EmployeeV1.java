package com.example.employeemanagement.dto;

import com.example.employeemanagement.pojos.Employee;
import java.math.BigDecimal;

public record EmployeeV1(String empId, String empName, String designation, BigDecimal salary) {
    public static EmployeeV1 from(Employee e) {
        return new EmployeeV1(e.empId(), e.empName(), e.designation(), e.salary());
    }
}
