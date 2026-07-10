package com.campswing.web;

import com.campswing.domain.event.EventCard;
import com.campswing.service.SettingsService;
import com.campswing.service.StaffService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class StaffController {

    private final StaffService service;
    private final SettingsService settings;
    private final GlobalModelAttributes.PageMetaHelper pageMeta;

    public StaffController(StaffService service, SettingsService settings,
                           GlobalModelAttributes.PageMetaHelper pageMeta) {
        this.service = service;
        this.settings = settings;
        this.pageMeta = pageMeta;
    }

    @GetMapping("/invited-dancers")
    public String invitedDancers(Model model) {
        pageMeta.apply(model, "staff.invited-dancers");
        model.addAttribute("legacyDancers", service.getLegacyDancers());
        model.addAttribute("specialGuestDancers", service.getSpecialGuestDancers());
        model.addAttribute("legacyComingSoon", settings.comingSoonFor("legacyDancers"));
        model.addAttribute("specialComingSoon", settings.comingSoonFor("specialGuestDancers"));
        return "staff/invited-dancers";
    }

    @GetMapping("/dj")
    public String djs(Model model) {
        pageMeta.apply(model, "staff.dj");
        model.addAttribute("djs", service.getAllDjs());
        model.addAttribute("comingSoon", settings.comingSoonFor("dj"));
        return "staff/dj";
    }

    @GetMapping("/instructors")
    public String instructors(Model model) {
        pageMeta.apply(model, "staff.instructors");
        model.addAttribute("instructors", service.getAllInstructors());
        model.addAttribute("comingSoon", settings.comingSoonFor("instructors"));
        return "staff/instructors";
    }

    @GetMapping("/staff")
    public String staff(Model model) {
        pageMeta.apply(model, "staff.staff");
        model.addAttribute("staff", service.getAllStaff());
        model.addAttribute("comingSoon", settings.comingSoonFor("staff"));
        return "staff/staff";
    }

    @GetMapping("/flea-market")
    public String fleaMarket(Model model) {
        pageMeta.apply(model, "flea-market");
        model.addAttribute("vendors", service.getAllFleaMarketVendors());
        model.addAttribute("comingSoon", settings.comingSoonFor("fleaMarket"));
        return "market/flea-market";
    }

    @GetMapping("/events")
    public String events(Model model) {
        pageMeta.apply(model, "events");
        model.addAttribute("eventCards", service.getAllEventCards());
        model.addAttribute("comingSoon", settings.comingSoonFor("events"));
        return "events/index";
    }

    @GetMapping("/events/{id}")
    public String eventDetail(@PathVariable String id, Model model) {
        EventCard eventCard = service.getEventCard(id);
        if (eventCard == null) {
            return "redirect:/events";
        }
        pageMeta.apply(model, "events");
        model.addAttribute("pageTitle", eventCard.title());
        model.addAttribute("eventCard", eventCard);
        model.addAttribute("comingSoon", settings.comingSoonFor("events"));
        return "events/detail";
    }
}
