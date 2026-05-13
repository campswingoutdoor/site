package com.campswing.domain.settings;

import java.time.LocalDate;

public record EventInfo(
        String name,
        String slogan,
        LocalDate startDate,
        LocalDate endDate,
        Venue mainVenue,
        Venue prePartyVenue,
        String contactEmail,
        String instagram,
        String bankName,
        String bankAccount,
        String accountHolder,
        String kakaoMapUrl,
        String heroSubtitle
) {
    public record Venue(String name, String address) {
    }
}
