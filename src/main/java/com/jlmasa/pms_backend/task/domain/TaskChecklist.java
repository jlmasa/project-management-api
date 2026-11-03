package com.jlmasa.pms_backend.task.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "task_checklist")
public class TaskChecklist {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id")

    private Task task;
    @Column(name = "title", nullable = false) private String title;

    @Column(name ="position", nullable = false) private int position;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected TaskChecklist(){}

    public TaskChecklist(Task task, String title, int position, Instant now){
        this.task = Objects.requireNonNull(task, "task");
        this.title = Objects.requireNonNull(title, "title");
        this.position = position;
        this.createdAt = now;
    }

    public void reoder(int newPosition) {this.position = newPosition;}

    public UUID getId() {
        return id;
    }

    public Task getTask() {
        return task;
    }

    public String getTitle() {
        return title;
    }

    public int getPosition() {
        return position;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
    @Override
    public boolean equals(Object o){
        if (this==o)return true;
        if(!(o instanceof TaskChecklist other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {return getClass().hashCode();}
}
