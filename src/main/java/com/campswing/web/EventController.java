package com.campswing.web;

import com.campswing.domain.settings.ScheduleItem;
import com.campswing.domain.settings.Weekday;
import com.campswing.service.SettingsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/event")
public class EventController {

    private final SettingsService settings;
    private final GlobalModelAttributes.PageMetaHelper pageMeta;

    public EventController(SettingsService settings, GlobalModelAttributes.PageMetaHelper pageMeta) {
        this.settings = settings;
        this.pageMeta = pageMeta;
    }

    @GetMapping("/overview")
    public String overview(Model model) {
        pageMeta.apply(model, "event.overview");
        model.addAttribute("scheduleByDay", groupByDay(settings.schedule()));
        return "event/overview";
    }

    @GetMapping("/venue")
    public String venue(Model model) {
        pageMeta.apply(model, "event.venue");
        model.addAttribute("venueDetails", settings.venueDetails());
        return "event/venue";
    }

    @GetMapping("/location")
    public String location(Model model) {
        pageMeta.apply(model, "event.location");
        model.addAttribute("pickupTrips", settings.pickupBus());
        return "event/location";
    }

    private static Map<Weekday, List<ScheduleItem>> groupByDay(List<ScheduleItem> items) {
        return items.stream().collect(Collectors.groupingBy(
                ScheduleItem::day,
                LinkedHashMap::new,
                Collectors.toList()));
    }
}
