package com.campswing.web;

import com.campswing.service.StaffService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaffController {

    private final StaffService service;

    public StaffController(StaffService service) {
        this.service = service;
    }

    @GetMapping("/dj")
    public String djs(Model model) {
        model.addAttribute("djs", service.getAllDjs());
        model.addAttribute("pageTitle", "DJ 소개");
        model.addAttribute("pageDescription", "Camp Swing Outdoor DJ Line-up — DJ CHORI · DJ ROYAL JELLY.");
        return "staff/dj";
    }

    @GetMapping("/instructors")
    public String instructors(Model model) {
        model.addAttribute("instructors", service.getAllInstructors());
        model.addAttribute("pageTitle", "강사 소개");
        model.addAttribute("pageDescription", "Camp Swing Outdoor 스윙 댄스 워크숍 강사 라인업.");
        return "staff/instructors";
    }

    @GetMapping("/staff")
    public String staff(Model model) {
        model.addAttribute("staff", service.getAllStaff());
        model.addAttribute("pageTitle", "스태프 소개");
        model.addAttribute("pageDescription", "Camp Swing Outdoor를 만드는 사람들.");
        return "staff/staff";
    }
}
