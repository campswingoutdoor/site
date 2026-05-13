package com.campswing.web;

import com.campswing.service.SettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = EventController.class,
        excludeAutoConfiguration = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SettingsService settingsService;

    @BeforeEach
    void setUp() {
        given(settingsService.event()).willReturn(HomeControllerTest.testEvent());
        given(settingsService.schedule()).willReturn(List.of());
        given(settingsService.pickupBus()).willReturn(List.of());
    }

    @Test
    void overview_returns200AndViewName() throws Exception {
        mockMvc.perform(get("/event/overview"))
                .andExpect(status().isOk())
                .andExpect(view().name("event/overview"))
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attributeExists("scheduleByDay"));
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
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attributeExists("pickupTrips"));
    }
}
