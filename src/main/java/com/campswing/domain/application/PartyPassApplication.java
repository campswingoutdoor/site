package com.campswing.domain.application;

import java.time.LocalDateTime;

public record PartyPassApplication(
        String id,
        LocalDateTime submittedAt,
        String applicantName,
        String phone,
        String email,
        PassType passType,
        TshirtSize tshirtSize,
        String dietaryNote,
        String memo,
        boolean agreedToTerms
) {
}
