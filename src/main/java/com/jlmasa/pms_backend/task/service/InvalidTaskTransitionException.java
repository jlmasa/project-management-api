package com.jlmasa.pms_backend.task.service;

import com.jlmasa.pms_backend.task.domain.TaskStatus;

public class InvalidTaskTransitionException extends RuntimeException {

    public InvalidTaskTransitionException(TaskStatus from, TaskStatus to){
        super("Cannot move a task from " + from + " to " + to+ ".");
    }
}
