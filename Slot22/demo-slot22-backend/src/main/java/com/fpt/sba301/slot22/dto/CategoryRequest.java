package com.fpt.sba301.slot22.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank(message = "Category name is required") @Size(max = 100) String categoryName,
        @NotBlank(message = "Description is required") @Size(max = 250) String categoryDescription,
        Short parentCategoryId,
        Boolean active
) {
}
