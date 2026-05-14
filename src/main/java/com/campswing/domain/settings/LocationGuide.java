package com.campswing.domain.settings;

public record LocationGuide(
        String transportTitle,
        String transportRoute,
        String duration,
        String roadAddress,
        String pickupHeadline,
        String pickupDescription,
        String pickupNote1,
        String pickupNote2
) {
}
