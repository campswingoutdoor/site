package com.campswing.api.advice;

import com.campswing.api.dto.ApiResponse;
import com.campswing.common.exception.BusinessException;
import com.campswing.common.exception.ErrorCode;
import com.campswing.common.exception.SheetsApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.campswing.api")
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException e) {
        Map<String, Object> details = new LinkedHashMap<>();
        for (FieldError fe : e.getBindingResult().getFieldErrors()) {
            details.putIfAbsent(fe.getField(), fe.getDefaultMessage());
        }
        return ResponseEntity.badRequest()
                .body(ApiResponse.fail(
                        ErrorCode.VALIDATION_ERROR.code(),
                        ErrorCode.VALIDATION_ERROR.defaultMessage(),
                        details));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotReadable(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.fail(
                        ErrorCode.BAD_REQUEST.code(),
                        "요청 본문을 해석할 수 없습니다.",
                        null));
    }

    @ExceptionHandler(SheetsApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleSheets(SheetsApiException e) {
        log.error("Sheets API error", e);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ApiResponse.fail(
                        e.errorCode().code(),
                        e.errorCode().defaultMessage(),
                        null));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException e) {
        log.warn("Business exception: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.fail(
                        e.errorCode().code(),
                        e.getMessage(),
                        null));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(NoHandlerFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail(
                        ErrorCode.NOT_FOUND.code(),
                        ErrorCode.NOT_FOUND.defaultMessage(),
                        null));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.fail(
                        ErrorCode.BAD_REQUEST.code(),
                        "지원하지 않는 HTTP 메서드입니다.",
                        null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAny(Exception e) {
        log.error("Unhandled exception", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(
                        ErrorCode.INTERNAL_ERROR.code(),
                        ErrorCode.INTERNAL_ERROR.defaultMessage(),
                        null));
    }
}
