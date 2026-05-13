package com.campswing.domain.staff;

public record Dj(
        String id,
        String stageName,
        String nickname,
        String role,
        String profileImagePath,
        String bio,
        int displayOrder
) {
}
