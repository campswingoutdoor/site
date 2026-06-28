package com.campswing.domain.application;

import java.time.LocalDateTime;

public record CampsiteApplication(
        String id,
        LocalDateTime submittedAt,
        String realName,
        String nickname,
        String phone,
        String email,
        int partySize,
        ArrivalTime arrivalTime,
        boolean usePickupBus,
        int totalPrice,
        String memo,
        boolean agreedToTerms
) {
}
