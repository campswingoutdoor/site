package com.campswing.web;

import com.campswing.service.SettingsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final SettingsService settings;

    public HomeController(SettingsService settings) {
        this.settings = settings;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("pageTitle", settings.event().name() + " — Swing Out Under The Stars");
        return "index";
    }
}
