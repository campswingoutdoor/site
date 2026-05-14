package com.campswing.api.dto;

import java.time.OffsetDateTime;

public record ApplicationCreatedResponse(
        String applicationId,
        OffsetDateTime submittedAt
) {
}
