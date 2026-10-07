package com.example.employeemanagement.dto;

import org.springframework.data.domain.Page;
import java.util.List;

public record PageResponse<T>(List<T> content, int page, int size, int numberOfElements,
        long totalElements, int totalPages, boolean hasNext, boolean hasPrevious) {
    public static <T> PageResponse<T> from(Page<T> p) {
        return new PageResponse<>(p.getContent(), p.getNumber(), p.getSize(), p.getNumberOfElements(),
                p.getTotalElements(), p.getTotalPages(),
                (long) p.getNumber() + 1 < p.getTotalPages(), p.hasPrevious());
    }
}
