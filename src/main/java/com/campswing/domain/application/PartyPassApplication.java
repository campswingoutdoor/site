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
        boolean applyWorkshop,
        VehicleUsage vehicleUsage,
        String vehicleNumber,
        int totalPrice,
        String memo,
        boolean agreedToTerms,
        String priceTier
) {
}
