package com.fpt.sba301.slot22.dto;

import jakarta.validation.constraints.NotNull;

public record NewsArticleUpdateRequest(@NotNull Short categoryId) {
}
