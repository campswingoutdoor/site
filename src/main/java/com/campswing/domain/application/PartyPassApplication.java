package com.campswing.domain.application;

import java.time.LocalDateTime;

public record PartyPassApplication(
        String id,
        LocalDateTime submittedAt,
        String realName,
        String nickname,
        String phone,
        String email,
        PassType passType,
        String club,
        DanceRole role,
        boolean useVehicle,
        String vehicleNumber,
        String dietaryNote,
        String memo,
        boolean agreedToTerms
) {
}
