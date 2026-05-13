package com.campswing.web;

import com.campswing.config.EventProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final EventProperties event;

    public HomeController(EventProperties event) {
        this.event = event;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("event", event);
        model.addAttribute("pageTitle", event.getName() + " — Swing Out Under The Stars");
        return "index";
    }
}
