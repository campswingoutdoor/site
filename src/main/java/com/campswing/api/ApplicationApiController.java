package com.campswing.api;

import com.campswing.api.dto.ApiResponse;
import com.campswing.api.dto.ApplicationCreatedResponse;
import com.campswing.api.dto.CampsiteApplicationRequest;
import com.campswing.api.dto.DormitoryApplicationRequest;
import com.campswing.api.dto.PartyPassApplicationRequest;
import com.campswing.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationApiController {

    private final ApplicationService service;

    public ApplicationApiController(ApplicationService service) {
        this.service = service;
    }

    @PostMapping("/party-pass")
    public ResponseEntity<ApiResponse<ApplicationCreatedResponse>> partyPass(
            @Valid @RequestBody PartyPassApplicationRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(service.submitPartyPass(req)));
    }

    @PostMapping("/campsite")
    public ResponseEntity<ApiResponse<ApplicationCreatedResponse>> campsite(
            @Valid @RequestBody CampsiteApplicationRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(service.submitCampsite(req)));
    }

    @PostMapping("/dormitory")
    public ResponseEntity<ApiResponse<ApplicationCreatedResponse>> dormitory(
            @Valid @RequestBody DormitoryApplicationRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(service.submitDormitory(req)));
    }
}
