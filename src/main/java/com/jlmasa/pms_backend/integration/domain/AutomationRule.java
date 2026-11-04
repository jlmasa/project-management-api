package com.jlmasa.pms_backend.integration.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "automation_rules")
public class AutomationRule {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "organization_id", nullable = false, updatable = false)
    private UUID organizationId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "trigger_type", nullable = false, updatable = false, length= 50)
    private String triggerType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "trigger_config", nullable = false)
    private String triggerConfig;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "action_config", nullable = false)
    private String actionConfig;

    @Column(name = "is_enabled", nullable = false)
    private boolean enabled;

    @Column(name = "created_by_membership_id", nullable = false, updatable = false)
    private UUID createdByMembershipId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updateAt;

    protected AutomationRule(){}

    public AutomationRule(UUID organizationId, String name, String triggerType, String triggerConfigJson, String actionConfigJson, UUID createdByMembershipId, Instant now){
        this.organizationId = Objects.requireNonNull(organizationId, "organizationId");
        this.name = Objects.requireNonNull(name, "name");
        this.triggerType = Objects.requireNonNull(triggerType, "triggerType");
        this.triggerConfig = Objects.requireNonNull(triggerConfigJson, "triggerConfigJson");
        this.actionConfig = Objects.requireNonNull(actionConfigJson, "actionConfigJson");
        this.enabled = true;
        this.createdByMembershipId = Objects.requireNonNull(createdByMembershipId, "createdByMembershipId");
        this.createdAt = now;
        this.updateAt = now;
    }

    public void disable(Instant now){this.enabled =false; this.updateAt = now;}
    public void updateConfig(String triggerConfigJson, String actionConfigJson, Instant now){
        this.triggerConfig = triggerConfigJson;
        this.actionConfig = actionConfigJson;
        this.updateAt = now;
    }

    public UUID getId() {return id;}
    public UUID getOrganizationId() {return organizationId;}
    public String getName() {return name;}
    public String getDescription() {return description;}
    public String getTriggerType() {return triggerType;}
    public String getTriggerConfig() {return triggerConfig;}
    public String getActionConfig() {return actionConfig;}
    public boolean isEnabled() {return enabled;}
    public UUID getCreatedByMembershipId() {return createdByMembershipId;}
    public Instant getCreatedAt() {return createdAt;}
    public Instant getUpdateAt() {return updateAt;}

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof AutomationRule other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {return getClass().hashCode();}
}
