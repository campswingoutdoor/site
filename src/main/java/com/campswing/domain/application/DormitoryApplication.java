package com.campswing.domain.application;

import java.time.LocalDateTime;

public record DormitoryApplication(
        String id,
        LocalDateTime submittedAt,
        String realName,
        String nickname,
        String phone,
        String email,
        Gender gender,
        Nights nights,
        int totalPrice,
        String memo,
        boolean agreedToTerms
) {
}
