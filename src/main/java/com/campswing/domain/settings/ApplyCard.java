package com.campswing.domain.settings;

public record ApplyCard(
        int displayOrder,
        String key,
        String stepLabel,
        String title,
        String description,
        String linkPath
) {
}
