package com.fpt.sba301.slot22.exception;

import java.time.Instant;

public record ApiError(String code, String message, Instant timestamp) {
}
