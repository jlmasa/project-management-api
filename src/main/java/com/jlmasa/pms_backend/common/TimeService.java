package com.jlmasa.pms_backend.common;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class TimeService {

    public static final ZoneId MANILA = ZoneId.of("Asia/Manila");

    private final Clock clock;

    public TimeService(Clock clock){
        this.clock = clock;
    }

    public Instant now() {
        return clock.instant();
    }

    public String toIsoUtc(Instant instant){
        return instant == null ? null : DateTimeFormatter.ISO_INSTANT.format(instant);
    }

    public String toManilaDisplay(Instant instant){
        return instant == null ? null : instant.atZone(MANILA).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm 'PHT'"));
    }
}
