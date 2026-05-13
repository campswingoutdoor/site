package com.campswing.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = EventController.class,
        excludeAutoConfiguration = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class},
        properties = {
                "event.name=Camp Swing Outdoor 2026",
                "event.start-date=2026-10-30",
                "event.end-date=2026-10-31",
                "event.main-venue.name=상주우산오토캠핑장",
                "event.main-venue.address=경북 상주시 외서면 우산리 223-3",
                "event.pre-party-venue.name=느티나무 카페",
                "event.pre-party-venue.address=서울"
        })
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void overview_returns200AndViewName() throws Exception {
        mockMvc.perform(get("/event/overview"))
                .andExpect(status().isOk())
                .andExpect(view().name("event/overview"))
                .andExpect(model().attributeExists("event"));
    }

    @Test
    void venue_returns200AndViewName() throws Exception {
        mockMvc.perform(get("/event/venue"))
                .andExpect(status().isOk())
                .andExpect(view().name("event/venue"))
                .andExpect(model().attributeExists("event"));
    }

    @Test
    void location_returns200AndViewName() throws Exception {
        mockMvc.perform(get("/event/location"))
                .andExpect(status().isOk())
                .andExpect(view().name("event/location"))
                .andExpect(model().attributeExists("event"));
    }
}
