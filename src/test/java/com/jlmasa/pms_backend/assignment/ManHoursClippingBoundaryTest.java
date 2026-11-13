package com.jlmasa.pms_backend.assignment;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ManHoursClippingBoundaryTest {

    @Test
    void sessionCrossingManilaMidnightCountsInBothManilaDays() {
        Instant start = Instant.parse("2026-10-27T15:30:00Z");
        Instant end = Instant.parse("2026-10-27T16:30:00Z");

        assertThat(java.time.Duration.between(start,end).toMinutes()).isEqualTo(60);
    }
}
