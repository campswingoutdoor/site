package com.campswing.common.exception;

public class SheetsApiException extends BusinessException {

    public SheetsApiException(String message) {
        super(ErrorCode.SHEETS_API_ERROR, message);
    }

    public SheetsApiException(String message, Throwable cause) {
        super(ErrorCode.SHEETS_API_ERROR, message, cause);
    }
}
