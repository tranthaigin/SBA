package com.fpt.sba301.slot22.dto;

public record CategoryResponse(
        Short categoryId,
        String categoryName,
        String categoryDescription,
        Short parentCategoryId,
        Boolean active
) {
}
