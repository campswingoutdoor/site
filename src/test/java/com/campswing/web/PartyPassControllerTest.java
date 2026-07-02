package com.campswing.web;

import com.campswing.api.dto.ApplicationCreatedResponse;
import com.campswing.api.dto.PartyPassApplicationRequest;
import com.campswing.config.SecurityConfig;
import com.campswing.domain.settings.ComingSoonItem;
import com.campswing.service.ApplicationService;
import com.campswing.service.SettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = PartyPassController.class)
@Import({SecurityConfig.class, GlobalModelAttributes.PageMetaHelper.class})
class PartyPassControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApplicationService applicationService;

    @MockBean
    private SettingsService settingsService;

    @BeforeEach
    void setUp() {
        given(settingsService.event()).willReturn(HomeControllerTest.testEvent());
        given(settingsService.partyPassBenefits()).willReturn(List.of());
        given(settingsService.locationGuide()).willReturn(HomeControllerTest.testLocationGuide());
        given(settingsService.conceptCopy()).willReturn(HomeControllerTest.testConceptCopy());
        given(settingsService.pageMeta(anyString())).willReturn(HomeControllerTest.testPageMeta());
        given(settingsService.comingSoonFor(anyString())).willReturn(new ComingSoonItem("test", "COMING SOON", "test"));
    }

    @Test
    void getForm_returns200AndAttributes() throws Exception {
        mockMvc.perform(get("/party-pass"))
                .andExpect(status().isOk())
                .andExpect(view().name("party-pass/index"))
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attributeExists("passTypes"))
                .andExpect(model().attributeExists("roles"))
                .andExpect(model().attributeExists("benefits"))
                .andExpect(model().attributeExists("event"));
    }

    @Test
    void postValidForm_redirectsToSuccess() throws Exception {
        given(applicationService.submitPartyPass(any(PartyPassApplicationRequest.class)))
                .willReturn(new ApplicationCreatedResponse(
                        "11111111-1111-1111-1111-111111111111",
                        OffsetDateTime.of(2026, 10, 30, 12, 0, 0, 0, ZoneOffset.ofHours(9))));

        mockMvc.perform(post("/party-pass")
                        .with(csrf())
                        .param("realName", "홍길동")
                        .param("nickname", "길동")
                        .param("phone", "010-1234-5678")
                        .param("email", "hong@example.com")
                        .param("passType", "FULL")
                        .param("club", "스윙홀릭")
                        .param("role", "LEADER")
                        .param("applyWorkshop", "false")
                        .param("vehicleUsage", "NONE")
                        .param("memo", "")
                        .param("agreedToTerms", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/apply/success*type=PARTY_PASS*id=*"));
    }

    @Test
    void postInvalidEmail_rerendersForm() throws Exception {
        mockMvc.perform(post("/party-pass")
                        .with(csrf())
                        .param("realName", "홍길동")
                        .param("nickname", "길동")
                        .param("phone", "010-1234-5678")
                        .param("email", "not-an-email")
                        .param("passType", "FULL")
                        .param("role", "LEADER")
                        .param("vehicleUsage", "NONE")
                        .param("agreedToTerms", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("party-pass/index"))
                .andExpect(model().attributeHasFieldErrors("form", "email"));
    }

    @Test
    void postWithoutTermsAgreement_rerendersForm() throws Exception {
        mockMvc.perform(post("/party-pass")
                        .with(csrf())
                        .param("realName", "홍길동")
                        .param("nickname", "길동")
                        .param("phone", "010-1234-5678")
                        .param("email", "hong@example.com")
                        .param("passType", "FULL")
                        .param("role", "LEADER")
                        .param("vehicleUsage", "NONE")
                        .param("agreedToTerms", "false"))
                .andExpect(status().isOk())
                .andExpect(view().name("party-pass/index"))
                .andExpect(model().attributeHasFieldErrors("form", "agreedToTerms"));
    }
}
