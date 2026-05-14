package com.campswing.domain.settings;

import java.time.LocalTime;

public record ScheduleItem(
        int displayOrder,
        Weekday day,
        LocalTime startTime,
        LocalTime endTime,
        String title,
        String description
) {
}
