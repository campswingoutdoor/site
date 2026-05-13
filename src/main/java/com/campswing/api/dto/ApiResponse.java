package com.campswing.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        T data,
        ErrorBody error,
        OffsetDateTime timestamp
) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null, OffsetDateTime.now());
    }

    public static <T> ApiResponse<T> empty() {
        return new ApiResponse<>(true, null, null, OffsetDateTime.now());
    }

    public static <T> ApiResponse<T> fail(String code, String message, Map<String, Object> details) {
        return new ApiResponse<>(false, null, new ErrorBody(code, message, details), OffsetDateTime.now());
    }

    public record ErrorBody(String code, String message, Map<String, Object> details) {
    }
}
