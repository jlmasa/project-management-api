package com.jlmasa.pms_backend.task.service;

import com.jlmasa.pms_backend.task.domain.TaskStatus;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public final class TaskTransitionTable {

    private static final Map<TaskStatus, Set<TaskStatus>> ALLOWED = new EnumMap<>(TaskStatus.class);

    static {
        ALLOWED.put(TaskStatus.BACKLOG, EnumSet.of(TaskStatus.TO_DO, TaskStatus.CANCELLED));
        ALLOWED.put(TaskStatus.TO_DO, EnumSet.of(TaskStatus.IN_PROGRESS, TaskStatus.BLOCKED, TaskStatus.BACKLOG, TaskStatus.CANCELLED));
        ALLOWED.put(TaskStatus.IN_PROGRESS, EnumSet.of(TaskStatus.IN_REVIEW, TaskStatus.BACKLOG, TaskStatus.TO_DO, TaskStatus.CANCELLED));
        ALLOWED.put(TaskStatus.IN_REVIEW, EnumSet.of(TaskStatus.COMPLETED, TaskStatus.IN_PROGRESS, TaskStatus.BLOCKED, TaskStatus.CANCELLED));
        ALLOWED.put(TaskStatus.BLOCKED, EnumSet.of(TaskStatus.TO_DO, TaskStatus.IN_PROGRESS, TaskStatus.CANCELLED));
        ALLOWED.put(TaskStatus.COMPLETED, EnumSet.of(TaskStatus.TO_DO));
        ALLOWED.put(TaskStatus.CANCELLED, EnumSet.of(TaskStatus.TO_DO));
    }
    private TaskTransitionTable() {}

    public static boolean isAllowed(TaskStatus from, TaskStatus to){
        return ALLOWED.getOrDefault(from, Set.of()).contains(to);
    }

    public static boolean isReopen(TaskStatus from, TaskStatus to){
        return (from == TaskStatus.COMPLETED || from == TaskStatus.CANCELLED) && to == TaskStatus.TO_DO;
    }

    public static boolean isClosing(TaskStatus to){
        return to == TaskStatus.COMPLETED || to == TaskStatus.CANCELLED;
    }
}
