package com.jlmasa.pms_backend.assignment.web;

import com.jlmasa.pms_backend.assignment.dto.CreateTimeAdjustmentRequest;
import com.jlmasa.pms_backend.assignment.service.TimeAdjustmentService;
import com.jlmasa.pms_backend.security.PmsPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/time-adjustment")
public class TimeAdjustmentController {

    private final TimeAdjustmentService timeAdjustmentService;

    public TimeAdjustmentController(TimeAdjustmentService timeAdjustmentService){
        this.timeAdjustmentService = timeAdjustmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(PmsPrincipal principal, @Valid @RequestBody CreateTimeAdjustmentRequest request){
        timeAdjustmentService.create(principal, request.workSessionId(), request.adjustmentSeconds(),request.reason());
    }
}
