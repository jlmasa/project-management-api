package com.jlmasa.pms_backend.calendar.domain;


import com.jlmasa.project_management.api.identity.entity.Organization_memberships;
import com.jlmasa.project_management.api.task.entity.Tasks;
import com.jlmasa.project_management.api.timetracking.entity.Work_sessions;
import jakarta.persistence.*;

import java.math.BigInteger;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "time_adjustments")
public class Time_adjustments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id") private UUID id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_session_id", nullable = false)
    private com.jlmasa.project_management.api.timetracking.entity.Work_sessions work_sessions;

    @Column(name = "adjustment_seconds", nullable = false) private Integer adjustment_seconds;
    @Column(name = "duration_before_seconds", nullable = false) private BigInteger duration_before_seconds;
    @Column(name = "duration_after_seconds", nullable = false) private BigInteger duration_after_seconds;
    @Column(name = "reason", nullable = false) private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "corrected_by_membership_id")
    private Organization_memberships organization_memberships;

    @Column(name = "corrected_at", nullable = false) private Instant corrected_at;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public com.jlmasa.project_management.api.timetracking.entity.Work_sessions getWork_sessions() {
        return work_sessions;
    }

    public void setWork_sessions(Work_sessions work_sessions) {
        this.work_sessions = work_sessions;
    }

    public Integer getAdjustment_seconds() {
        return adjustment_seconds;
    }

    public void setAdjustment_seconds(Integer adjustment_seconds) {
        this.adjustment_seconds = adjustment_seconds;
    }

    public BigInteger getDuration_before_seconds() {
        return duration_before_seconds;
    }

    public void setDuration_before_seconds(BigInteger duration_before_seconds) {
        this.duration_before_seconds = duration_before_seconds;
    }

    public BigInteger getDuration_after_seconds() {
        return duration_after_seconds;
    }

    public void setDuration_after_seconds(BigInteger duration_after_seconds) {
        this.duration_after_seconds = duration_after_seconds;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Organization_memberships getOrganization_memberships() {
        return organization_memberships;
    }

    public void setOrganization_memberships(Organization_memberships organization_memberships) {
        this.organization_memberships = organization_memberships;
    }

    public Instant getCorrected_at() {
        return corrected_at;
    }

    public void setCorrected_at(Instant corrected_at) {
        this.corrected_at = corrected_at;
    }
}
