package com.campswing.api.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * Web (Thymeleaf) 컨트롤러 전용 예외 핸들러.
 * SSR 환경이므로 뷰 이름을 반환합니다. @ResponseBody를 붙이지 마세요.
 * (API 전용 핸들러는 com.campswing.api.advice.ApiExceptionHandler 참고)
 */
@ControllerAdvice(basePackages = "com.campswing.web")
public class WebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(WebExceptionHandler.class);

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFound(NoHandlerFoundException e, Model model) {
        model.addAttribute("message", "요청하신 페이지를 찾을 수 없습니다.");
        return "error/404";
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String handleAny(Exception e, Model model) {
        log.error("Unhandled web exception", e);
        model.addAttribute("message", "잠시 후 다시 시도해주세요.");
        return "error/500";
    }
}
