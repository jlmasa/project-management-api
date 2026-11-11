package com.jlmasa.pms_backend.task.web;

import com.jlmasa.pms_backend.security.PmsPrincipal;
import com.jlmasa.pms_backend.task.domain.TaskStatus;
import com.jlmasa.pms_backend.task.service.TaskStatusService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks/{taskId}/status")
public class TaskStatusController {

    private final TaskStatusService taskStatusService;

    public TaskStatusController(TaskStatusService taskStatusService){
        this.taskStatusService = taskStatusService;
    }

    public record TransitionRequest(TaskStatus targetStatus){
    }

    @PostMapping
    public void transition(PmsPrincipal principal, @PathVariable UUID taskId, @RequestBody TransitionRequest request){
        taskStatusService.transition(principal, taskId, request.targetStatus());
    }
}
