package com.campswing.web;

import com.campswing.domain.settings.ComingSoonItem;
import com.campswing.domain.settings.ConceptCopy;
import com.campswing.domain.settings.EventInfo;
import com.campswing.domain.settings.LocationGuide;
import com.campswing.domain.settings.PageMeta;
import com.campswing.service.SettingsService;
import com.campswing.web.GlobalModelAttributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = HomeController.class,
        excludeAutoConfiguration = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
@Import(GlobalModelAttributes.PageMetaHelper.class)
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
        given(settingsService.indexHighlights()).willReturn(List.of());
        given(settingsService.venueDetails()).willReturn(List.of());
        given(settingsService.applyCards()).willReturn(List.of());
        given(settingsService.locationGuide()).willReturn(testLocationGuide());
        given(settingsService.conceptCopy()).willReturn(testConceptCopy());
        given(settingsService.pageMeta(anyString())).willReturn(testPageMeta());
        given(settingsService.comingSoonFor(anyString())).willReturn(new ComingSoonItem("test", "COMING SOON", "test"));
    }

    static PageMeta testPageMeta() {
        return new PageMeta("home", "홈", "CAMP SWING OUTDOOR", "테스트 페이지", "테스트 설명");
    }

    static LocationGuide testLocationGuide() {
        return new LocationGuide("대중교통 안내", "동서울→상주", "약 3시간", "도로명", "PICKUP", "픽업 안내", "주의1", "주의2");
    }

    static ConceptCopy testConceptCopy() {
        return new ConceptCopy("컨셉", "태그라인", "1박 2일", "* 변경 가능");
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
                "Swing Dance · Camping · Music · Community",
                "신청자 이름 로 입금해주세요",
                "행사 2주 전 (운영팀 공지에 따름)",
                "STANDARD"
        );
    }
}
