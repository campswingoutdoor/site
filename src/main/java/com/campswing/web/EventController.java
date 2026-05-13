package com.campswing.web;

import com.campswing.config.EventProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/event")
public class EventController {

    private final EventProperties event;

    public EventController(EventProperties event) {
        this.event = event;
    }

    @GetMapping("/overview")
    public String overview(Model model) {
        model.addAttribute("event", event);
        model.addAttribute("pageTitle", "행사 개요");
        model.addAttribute("pageDescription", "Camp Swing Outdoor 2026 행사 개요 — 스윙댄스와 캠핑이 만나는 1박 2일 야외 이벤트.");
        return "event/overview";
    }

    @GetMapping("/venue")
    public String venue(Model model) {
        model.addAttribute("event", event);
        model.addAttribute("pageTitle", "행사장 소개");
        model.addAttribute("pageDescription", "금요일 전야제(느티나무 카페)와 토요일 메인 행사장(상주우산오토캠핑장) 소개.");
        return "event/venue";
    }

    @GetMapping("/location")
    public String location(Model model) {
        model.addAttribute("event", event);
        model.addAttribute("pageTitle", "오시는 길");
        model.addAttribute("pageDescription", "상주우산오토캠핑장 위치, 픽업버스 시간표, 대중교통 안내.");
        return "event/location";
    }
}
