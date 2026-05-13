package com.campswing.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.LocalDate;

@Getter
@Setter
@ConfigurationProperties(prefix = "event")
public class EventProperties {

    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Venue mainVenue = new Venue();
    private Venue prePartyVenue = new Venue();

    @Getter
    @Setter
    public static class Venue {
        private String name;
        private String address;
    }
}
