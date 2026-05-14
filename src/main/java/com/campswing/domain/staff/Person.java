package com.campswing.domain.staff;

public record Person(
        String id,
        String name,
        String role,
        String profileImagePath,
        String bio,
        int displayOrder
) {
}
