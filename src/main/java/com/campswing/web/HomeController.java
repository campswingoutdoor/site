package com.campswing.web;

import com.campswing.service.SettingsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final SettingsService settings;
    private final GlobalModelAttributes.PageMetaHelper pageMeta;

    public HomeController(SettingsService settings, GlobalModelAttributes.PageMetaHelper pageMeta) {
        this.settings = settings;
        this.pageMeta = pageMeta;
    }

    @GetMapping("/")
    public String home(Model model) {
        pageMeta.apply(model, "home");
        // home 페이지는 행사명으로 title 동적 구성 (PageMeta.title이 비어 있을 때 폴백)
        if (!model.containsAttribute("pageTitle") || isBlank(model.getAttribute("pageTitle"))) {
            model.addAttribute("pageTitle", settings.event().name() + " — Swing Out Under The Stars");
        }
        model.addAttribute("indexHighlights", settings.indexHighlights());
        return "index";
    }

    private static boolean isBlank(Object o) {
        return o == null || o.toString().isBlank();
    }
}
