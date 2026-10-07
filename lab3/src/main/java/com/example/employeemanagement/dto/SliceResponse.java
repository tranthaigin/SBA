package com.example.employeemanagement.dto;

import org.springframework.data.domain.Slice;
import java.util.List;

public record SliceResponse<T>(List<T> content, int page, int size, int numberOfElements,
        boolean hasNext, boolean hasPrevious) {
    public static <T> SliceResponse<T> from(Slice<T> s) {
        return new SliceResponse<>(s.getContent(), s.getNumber(), s.getSize(), s.getNumberOfElements(),
                s.hasNext(), s.hasPrevious());
    }
}
