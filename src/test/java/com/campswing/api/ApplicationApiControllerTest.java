package com.campswing.api;

import com.campswing.api.advice.ApiExceptionHandler;
import com.campswing.api.dto.ApplicationCreatedResponse;
import com.campswing.api.dto.PartyPassApplicationRequest;
import com.campswing.common.exception.SheetsApiException;
import com.campswing.service.ApplicationService;
import com.campswing.service.SettingsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ApplicationApiController.class,
        excludeAutoConfiguration = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
@Import(ApiExceptionHandler.class)
class ApplicationApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ApplicationService applicationService;

    @MockBean
    private SettingsService settingsService;

    @Test
    void partyPass_validPayload_returns201() throws Exception {
        given(applicationService.submitPartyPass(any(PartyPassApplicationRequest.class)))
                .willReturn(new ApplicationCreatedResponse(
                        "11111111-1111-1111-1111-111111111111",
                        OffsetDateTime.of(2026, 10, 30, 12, 0, 0, 0, ZoneOffset.ofHours(9))));

        Map<String, Object> body = Map.of(
                "realName", "홍길동",
                "nickname", "길동",
                "phone", "010-1234-5678",
                "email", "hong@example.com",
                "passType", "FULL",
                "role", "LEADER",
                "agreedToTerms", true
        );

        mockMvc.perform(post("/api/v1/applications/party-pass")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.applicationId").exists());
    }

    @Test
    void partyPass_invalidEmail_returns400ValidationError() throws Exception {
        Map<String, Object> body = Map.of(
                "realName", "홍길동",
                "nickname", "길동",
                "phone", "010-1234-5678",
                "email", "not-an-email",
                "passType", "FULL",
                "role", "LEADER",
                "agreedToTerms", true
        );

        mockMvc.perform(post("/api/v1/applications/party-pass")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.error.details.email").exists());
    }

    @Test
    void partyPass_agreedToTermsFalse_returns400() throws Exception {
        Map<String, Object> body = Map.of(
                "realName", "홍길동",
                "nickname", "길동",
                "phone", "010-1234-5678",
                "email", "hong@example.com",
                "passType", "FULL",
                "role", "LEADER",
                "agreedToTerms", false
        );

        mockMvc.perform(post("/api/v1/applications/party-pass")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.error.details.agreedToTerms").exists());
    }

    @Test
    void partyPass_sheetsApiFailure_returns502() throws Exception {
        given(applicationService.submitPartyPass(any(PartyPassApplicationRequest.class)))
                .willThrow(new SheetsApiException("sheets down"));

        Map<String, Object> body = Map.of(
                "realName", "홍길동",
                "nickname", "길동",
                "phone", "010-1234-5678",
                "email", "hong@example.com",
                "passType", "FULL",
                "role", "LEADER",
                "agreedToTerms", true
        );

        mockMvc.perform(post("/api/v1/applications/party-pass")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("SHEETS_API_ERROR"));
    }
}
