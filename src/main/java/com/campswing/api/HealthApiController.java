package com.campswing.api;

import com.campswing.api.dto.ApiResponse;
import com.campswing.service.sheets.GoogleSheetsClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class HealthApiController {

    private final GoogleSheetsClient sheetsClient;

    public HealthApiController(GoogleSheetsClient sheetsClient) {
        this.sheetsClient = sheetsClient;
    }

    @GetMapping("/health")
    public ApiResponse<Map<String, String>> health() {
        Map<String, String> body = Map.of(
                "status", "UP",
                "sheets", sheetsClient.isEnabled() ? "REACHABLE" : "DISABLED"
        );
        return ApiResponse.ok(body);
    }
}
