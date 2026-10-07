package com.example.employeemanagement.repositories;

import com.example.employeemanagement.exceptions.*;
import com.example.employeemanagement.pojos.Employee;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

@Repository
public class EmployeeRepository implements IEmployeeRepository {
    private final List<Employee> employees = new ArrayList<>();

    public EmployeeRepository() {
        String[] names = {"An", "Binh", "Chi", "Dung", "Giang", "Hoa", "Khanh", "Linh", "Minh", "Nam"};
        String[] roles = {"Developer", "Tester", "HR"};
        for (int i = 1; i <= 30; i++) {
            employees.add(new Employee("E%03d".formatted(i), names[(i - 1) % names.length],
                    roles[(i - 1) % roles.length], BigDecimal.valueOf(10_000_000L + i * 250_000L),
                    "employee%02d@example.com".formatted(i), i % 3 == 0 ? "People" : "Engineering"));
        }
    }

    @Override
    public synchronized Page<Employee> getAllEmployees(Pageable pageable) {
        List<Employee> ordered = sorted(pageable.getSort());
        List<Employee> content = ordered.stream().skip(pageable.getOffset()).limit(pageable.getPageSize()).toList();
        // PageImpl.hasNext() adds 1 to an int page index; MAX_VALUE would overflow.
        return new PageImpl<>(content, pageable, ordered.size()) {
            @Override public boolean hasNext() {
                return pageable.getOffset() + getNumberOfElements() < getTotalElements();
            }
        };
    }

    @Override
    public synchronized Slice<Employee> getEmployeeSlice(Pageable pageable) {
        // Fetch size+1 to detect a next slice. No count/total metadata is calculated for this contract.
        List<Employee> window = sorted(pageable.getSort()).stream().skip(pageable.getOffset())
                .limit((long) pageable.getPageSize() + 1).toList();
        boolean hasNext = window.size() > pageable.getPageSize();
        return new SliceImpl<>(hasNext ? window.subList(0, pageable.getPageSize()) : window, pageable, hasNext);
    }

    @Override
    public synchronized Employee getEmployeeById(String id) {
        return employees.stream().filter(e -> e.empId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public synchronized Employee createEmployee(Employee employee) {
        if (getEmployeeById(employee.empId()) != null) {
            throw new ApiException(HttpStatus.CONFLICT, "Employee " + employee.empId() + " already exists");
        }
        employees.add(employee);
        return employee;
    }

    @Override
    public synchronized Employee updateEmployee(String id, Employee employee) {
        Employee existing = getEmployeeById(id);
        if (existing == null) { throw new EmployeeNotFoundException(id); }
        if (!id.equals(employee.empId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Path empId must match body empId");
        }
        employees.set(employees.indexOf(existing), employee);
        return employee;
    }

    @Override
    public synchronized void deleteEmployee(String id) {
        if (!employees.removeIf(e -> e.empId().equals(id))) { throw new EmployeeNotFoundException(id); }
    }

    private List<Employee> sorted(Sort sort) {
        Comparator<Employee> comparator = (a, b) -> 0;
        for (Sort.Order order : sort) {
            Comparator<Employee> next = switch (order.getProperty()) {
                case "empId" -> Comparator.comparing(Employee::empId);
                case "empName" -> Comparator.comparing(Employee::empName);
                case "designation" -> Comparator.comparing(Employee::designation);
                case "salary" -> Comparator.comparing(Employee::salary);
                case "email" -> Comparator.comparing(Employee::email, Comparator.nullsLast(Comparator.naturalOrder()));
                case "department" -> Comparator.comparing(Employee::department, Comparator.nullsLast(Comparator.naturalOrder()));
                default -> throw new ApiException(HttpStatus.BAD_REQUEST, "Unknown sort property");
            };
            comparator = comparator.thenComparing(order.isDescending() ? next.reversed() : next);
        }
        return employees.stream().sorted(comparator.thenComparing(Employee::empId)).toList();
    }
}
