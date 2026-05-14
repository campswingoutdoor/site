package com.campswing.common.util;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Component
public class KstClock {

    public static final ZoneId KST = ZoneId.of("Asia/Seoul");
    public static final ZoneOffset KST_OFFSET = ZoneOffset.ofHours(9);

    private final Clock clock;

    public KstClock() {
        this(Clock.system(KST));
    }

    KstClock(Clock clock) {
        this.clock = clock;
    }

    public LocalDateTime now() {
        return LocalDateTime.now(clock);
    }

    public OffsetDateTime nowOffset() {
        return OffsetDateTime.now(clock);
    }
}
