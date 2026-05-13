package com.campswing.web;

import com.campswing.domain.settings.EventInfo;
import com.campswing.service.SettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = HomeController.class,
        excludeAutoConfiguration = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SettingsService settingsService;

    @BeforeEach
    void setUp() {
        given(settingsService.event()).willReturn(testEvent());
        given(settingsService.schedule()).willReturn(List.of());
        given(settingsService.pickupBus()).willReturn(List.of());
        given(settingsService.partyPassBenefits()).willReturn(List.of());
    }

    @Test
    void home_returnsIndexView() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attributeExists("pageTitle"));
    }

    static EventInfo testEvent() {
        return new EventInfo(
                "Camp Swing Outdoor 2026",
                "Swing Out Under The Stars",
                LocalDate.of(2026, 10, 30),
                LocalDate.of(2026, 10, 31),
                new EventInfo.Venue("상주우산오토캠핑장", "경북 상주시 외서면 우산리 223-3"),
                new EventInfo.Venue("느티나무 카페", "서울"),
                "contact@campswing.example",
                "@campswingoutdoor",
                "국민은행",
                "000-000-000000",
                "캠프스윙아웃도어",
                "",
                "Swing Dance · Camping · Music · Community"
        );
    }
}
