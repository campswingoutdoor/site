package com.campswing.web;

import com.campswing.config.EventProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/apply")
public class ApplyController {

    private final EventProperties event;

    public ApplyController(EventProperties event) {
        this.event = event;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("event", event);
        model.addAttribute("pageTitle", "바로 신청하기");
        return "apply/index";
    }

    @GetMapping("/success")
    public String success(@RequestParam(value = "type", required = false) String type,
                          @RequestParam(value = "id", required = false) String id,
                          Model model) {
        model.addAttribute("applicationType", type);
        model.addAttribute("applicationId", id);
        model.addAttribute("applicationTypeLabel", labelOf(type));
        model.addAttribute("pageTitle", "신청 완료");
        return "apply/success";
    }

    private static String labelOf(String type) {
        if (type == null) return "신청";
        return switch (type) {
            case "PARTY_PASS" -> "파티패스";
            case "CAMPSITE" -> "캠핑사이트";
            case "DORMITORY" -> "도미토리";
            default -> "신청";
        };
    }
}
