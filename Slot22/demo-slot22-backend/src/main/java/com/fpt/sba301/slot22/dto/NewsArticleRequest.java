package com.fpt.sba301.slot22.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NewsArticleRequest(
        @NotBlank @Size(max = 20) String newsArticleId,
        @NotNull Short categoryId
) {
}
