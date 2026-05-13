package com.campswing.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = HomeController.class,
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
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void home_returnsIndexView() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attributeExists("pageTitle"));
    }
}
