package com.example.employeemanagement.services;

import com.example.employeemanagement.exceptions.ApiException;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;

public final class PagingPolicy {
    private static final Set<String> ALLOWED = Set.of("empId", "empName", "designation", "salary", "email", "department");
    private PagingPolicy() {}

    public static Pageable create(int page, int size, String[] expressions) {
        if (page < 0 || size < 1 || size > 100) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "page must be >= 0 and size must be between 1 and 100");
        }
        List<Sort.Order> orders = new ArrayList<>();
        if (expressions == null || expressions.length == 0) { expressions = new String[]{"empId,asc"}; }
        Set<String> seen = new HashSet<>();
        for (String expression : expressions) {
            String[] parts = expression.split(",", -1);
            String property = parts[0].trim();
            if (parts.length > 2 || !ALLOWED.contains(property) || !seen.add(property)) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "sort must use distinct allowed properties: " + ALLOWED);
            }
            String direction = parts.length == 1 ? "asc" : parts[1].trim().toLowerCase(Locale.ROOT);
            if (!direction.equals("asc") && !direction.equals("desc")) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "sort direction must be asc or desc");
            }
            orders.add(new Sort.Order(direction.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, property));
        }
        if (!seen.contains("empId")) { orders.add(Sort.Order.asc("empId")); }
        return PageRequest.of(page, size, Sort.by(orders));
    }
}
