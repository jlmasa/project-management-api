package com.jlmasa.pms_backend.assignment.web;


import com.jlmasa.pms_backend.assignment.domain.TaskAssignment;
import com.jlmasa.pms_backend.assignment.dto.AssignTaskRequest;
import com.jlmasa.pms_backend.assignment.dto.AssignmentResponse;
import com.jlmasa.pms_backend.assignment.service.AssignmentService;
import com.jlmasa.pms_backend.security.PmsPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks/{taskId}/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService){
        this.assignmentService  = assignmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssignmentResponse assign(PmsPrincipal principal, @PathVariable UUID taskId, @Valid @RequestBody AssignTaskRequest request){
        return toResponse(assignmentService.assign(principal, taskId, request.assigneeMembershipId()));
    }

    @PostMapping("/reassign")
    public AssignmentResponse reassign(PmsPrincipal principal, @PathVariable UUID taskId,
                                       @Valid @RequestBody AssignTaskRequest request){
        return toResponse(assignmentService.reassign(principal, taskId, request.assigneeMembershipId()));
    }
    @GetMapping
    public List<AssignmentResponse> history (@PathVariable UUID taskId){
        return assignmentService.history(taskId).stream().map(AssignmentController::toResponse).toList();
    }

    private static AssignmentResponse toResponse(TaskAssignment a){
        return new AssignmentResponse(a.getId(),a.getAssignedToMembershipId(),a.getAssignedByMembershipId(),a.getAssignmentStatus(),a.getAssignedAt(),a.getEndedAt());
    }
}
