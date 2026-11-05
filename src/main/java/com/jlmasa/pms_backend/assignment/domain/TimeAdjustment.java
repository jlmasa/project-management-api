package com.jlmasa.pms_backend.assignment.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;


@Entity
@Table(name = "time_adjustments")
public class TimeAdjustment {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "work_session_id", nullable = false, updatable = false)
    private UUID workSessionId;

    @Column(name = "adjustment_seconds", nullable = false, updatable = false)
    private int adjustmentSeconds;

    @Column(name = "duration_before_seconds", nullable = false, updatable = false)
    private long durationBeforeSeconds;

    @Column(name = "duration_after_seconds", nullable = false, updatable = false)
    private long durationAfterSeconds;

    @Column(name = "reason", nullable = false, updatable = false)
    private String reason;

    @Column(name = "corrected_by_membership_id", nullable = false, updatable = false)
    private UUID correctedByMembershipId;

    @Column(name = "corrected_at", nullable = false, updatable = false)
    private Instant correctedAt;

    protected TimeAdjustment(){}

    public TimeAdjustment(UUID workSessionId, int adjustmentSeconds, long durationBeforeSeconds, String reason, UUID correctedByMembershipId, Instant now){
        this.workSessionId = Objects.requireNonNull(workSessionId, "workSessionId");
        this.adjustmentSeconds = adjustmentSeconds;
        this.durationBeforeSeconds = durationBeforeSeconds;
        this.durationAfterSeconds = durationBeforeSeconds + adjustmentSeconds;
        this.reason = Objects.requireNonNull(reason, "reason");
        this.correctedByMembershipId = Objects.requireNonNull(correctedByMembershipId, "correctedByMembershipId");
        this.correctedAt = now;
    }

    public UUID getId() {return id;}
    public UUID getWorkSessionId() {return workSessionId;}
    public int getAdjustmentSeconds() {return adjustmentSeconds;}
    public long getDurationBeforeSeconds() {return durationBeforeSeconds;}
    public long getDurationAfterSeconds() {return durationAfterSeconds;}
    public String getReason() {return reason;}
    public UUID getCorrectedByMembershipId() {return correctedByMembershipId;}
    public Instant getCorrectedAt() {return correctedAt;}

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof TimeAdjustment other)) return false;
        return id != null && id.equals(other.id);
    }
    @Override
    public int hashCode() {return getClass().hashCode();}
}
