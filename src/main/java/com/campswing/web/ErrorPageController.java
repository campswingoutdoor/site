package com.campswing.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorPageController {

    @GetMapping("/error/403")
    public String forbidden(Model model) {
        model.addAttribute("message", "요청을 처리할 수 없습니다. 페이지를 새로고침한 뒤 다시 시도해주세요.");
        return "error/403";
    }

    @GetMapping("/error/404")
    public String notFound(Model model) {
        model.addAttribute("message", "요청하신 페이지를 찾을 수 없습니다.");
        return "error/404";
    }

    @GetMapping("/error/500")
    public String serverError(Model model) {
        model.addAttribute("message", "잠시 후 다시 시도해주세요.");
        return "error/500";
    }
}
