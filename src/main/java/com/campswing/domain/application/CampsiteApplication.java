package com.campswing.domain.application;

import java.time.LocalDateTime;

public record CampsiteApplication(
        String id,
        LocalDateTime submittedAt,
        String applicantName,
        String phone,
        String email,
        int partySize,
        TentSize tentSize,
        ArrivalTime arrivalTime,
        boolean usePickupBus,
        String memo,
        boolean agreedToTerms
) {
}
