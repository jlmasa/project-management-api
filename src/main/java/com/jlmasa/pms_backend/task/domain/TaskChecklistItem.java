package com.jlmasa.pms_backend.task.domain;


import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "task_checklist_items")
public class TaskChecklistItem {

    @Id @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY,  optional = false)
    @JoinColumn(name = "checklist_id")
    private TaskChecklist checklist;

    @Column(name = "title", nullable = false) private String title;

    @Column(name="is_completed", nullable = false) private Boolean completed;


    @Column(name = "completed_by_membership_id")
    private UUID completedByMembershipId;

    @Column(name = "completed_at") private Instant completedAt;

    @Column(name = "position", nullable = false) private Integer position;

    protected TaskChecklistItem(){}

    public TaskChecklistItem(TaskChecklist checklist, String title, int position){
        this.checklist = Objects.requireNonNull(checklist, "checklist");
        this.title = Objects.requireNonNull(title, "title");
        this.position = position;
        this.completed = false;
    }
    public void markCompleted(UUID byMembershipId, Instant now){
        this.completed = true;
        this.completedByMembershipId = Objects.requireNonNull(byMembershipId, "byMembershipId");
        this.completedAt = now;
    }

    public void markIncomplete () {
        this.completed = false;
        this.completedByMembershipId = null;
        this.completedAt = null;
    }

    public UUID getId() {
        return id;
    }

    public TaskChecklist getChecklist() {
        return checklist;
    }

    public String getTitle() {
        return title;
    }

    public Boolean isCompleted() {
        return completed;
    }

    public UUID getCompletedByMembershipId() {
        return completedByMembershipId;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public Integer getPosition() {
        return position;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if(!(o instanceof TaskChecklistItem other)) return false;
        return id != null && id.equals(other.id);
    }
    @Override
    public int hashCode(){return getClass().hashCode();}
}
