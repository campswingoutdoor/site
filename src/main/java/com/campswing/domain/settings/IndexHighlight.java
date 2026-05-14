package com.campswing.domain.settings;

public record IndexHighlight(
        int displayOrder,
        String key,
        String label,
        String mainText,
        String subtext
) {
}
