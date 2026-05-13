package com.campswing.common.exception;

public enum ErrorCode {
    BAD_REQUEST("BAD_REQUEST", "잘못된 요청입니다."),
    VALIDATION_ERROR("VALIDATION_ERROR", "입력값이 올바르지 않습니다."),
    CSRF_FORBIDDEN("CSRF_FORBIDDEN", "보안 토큰이 일치하지 않습니다."),
    NOT_FOUND("NOT_FOUND", "리소스를 찾을 수 없습니다."),
    CONFLICT("CONFLICT", "중복된 데이터입니다."),
    TOO_MANY_REQUESTS("TOO_MANY_REQUESTS", "요청이 너무 잦습니다. 잠시 후 다시 시도해주세요."),
    INTERNAL_ERROR("INTERNAL_ERROR", "서버 오류가 발생했습니다."),
    SHEETS_API_ERROR("SHEETS_API_ERROR", "외부 시트 저장 중 오류가 발생했습니다.");

    private final String code;
    private final String defaultMessage;

    ErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public String code() {
        return code;
    }

    public String defaultMessage() {
        return defaultMessage;
    }
}
