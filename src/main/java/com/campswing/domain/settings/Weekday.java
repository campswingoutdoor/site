package com.campswing.domain.settings;

public enum Weekday {
    FRI("금요일"),
    SAT("토요일"),
    SUN("일요일");

    private final String label;

    Weekday(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
