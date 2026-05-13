package com.campswing.domain.settings;

import java.time.LocalTime;

public record PickupBusTrip(
        int displayOrder,
        LocalTime departureTime,
        String route,
        int pricePerSeat,
        String note
) {
}
