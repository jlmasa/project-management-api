package com.jlmasa.pms_backend.assignment.web;

import com.jlmasa.pms_backend.assignment.domain.WorkSession;
import com.jlmasa.pms_backend.assignment.dto.WorkSessionResponse;
import com.jlmasa.pms_backend.assignment.service.WorkSessionService;
import com.jlmasa.pms_backend.security.PmsPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping()
public class WorkSessionController {

    private final WorkSessionService workSessionService;

    public WorkSessionController(WorkSessionService workSessionService){
        this.workSessionService = workSessionService;
    }

    @PostMapping("/start")
    @ResponseStatus(HttpStatus.CREATED)
    public WorkSessionResponse start(PmsPrincipal principal, @PathVariable UUID assignmentId){
        return toResponse(workSessionService.start(principal, assignmentId), assignmentId);
    }

    @PostMapping("/pause")
    public WorkSessionResponse pause(PmsPrincipal  principal, @PathVariable UUID assignmentId){
        return toResponse(workSessionService.pause(principal, assignmentId),assignmentId);
    }

    @PostMapping("/resume")
    @ResponseStatus(HttpStatus.CREATED)
    public WorkSessionResponse resume(PmsPrincipal principal, @PathVariable UUID assignmentId){
        return toResponse(workSessionService.resume(principal, assignmentId), assignmentId);
    }

    @PostMapping
    public WorkSessionResponse stop(PmsPrincipal principal, @PathVariable UUID assignmentId){
        return toResponse(workSessionService.stop(principal, assignmentId),assignmentId);
    }

    private WorkSessionResponse toResponse(WorkSession session, UUID assignmentId){
        return new WorkSessionResponse(session.getId(), assignmentId, session.getSessionStatus(),
                session.getStartedAt(), session.getEndedAt(), session.getDurationSeconds(),workSessionService.runningTotalSeconds(assignmentId));
    }
}
