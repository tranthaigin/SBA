package com.fpt.sba301.slot22.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank(message = "Category name is required")
        @Size(max = 100, message = "Category name must be at most 100 characters")
        String categoryName,
        @NotBlank(message = "Description is required")
        @Size(max = 250, message = "Description must be at most 250 characters")
        String categoryDescription,
        Short parentCategoryId,
        Boolean active
) {
}
