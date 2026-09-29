package com.fpt.sba301.slot13.exception;

public record ApiError(
    int status,
    String error,
    String message
) { }
