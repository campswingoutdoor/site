package com.campswing.domain.settings;

public record VenueDetail(
        int displayOrder,
        String key,
        String badge,
        String title,
        String venueName,
        String description,
        String period
) {
}
