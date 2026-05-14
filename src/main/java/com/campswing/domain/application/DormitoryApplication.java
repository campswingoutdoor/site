package com.campswing.domain.application;

import java.time.LocalDateTime;

public record DormitoryApplication(
        String id,
        LocalDateTime submittedAt,
        String applicantName,
        String phone,
        String email,
        Gender gender,
        Nights nights,
        boolean usePickupBus,
        String roommatePreference,
        String memo,
        boolean agreedToTerms
) {
}
