package com.fpt.sba301.slot22.dto;

import java.time.Instant;

public record ApiError(String code, String message, Instant timestamp) {
}
