package com.jlmasa.pms_backend.security;

import com.jlmasa.pms_backend.audit.repository.AuditLogRepository;
import com.jlmasa.pms_backend.common.ApiError;
import com.jlmasa.pms_backend.notifications.domain.AuditLog;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final AuditLogRepository auditLogRepository;
    private final Clock clock;

    public RestAccessDeniedHandler(AuditLogRepository auditLogRepository, Clock clock){
        this.auditLogRepository = auditLogRepository;
        this.clock = clock;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex)throws java.io.IOException{
        Object principal = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication() != null ?
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal() : null;

        if(principal instanceof PmsPrincipal pmsPrincipal){
            AuditLog event = new AuditLog(pmsPrincipal.organizationId(), "ACCESS_DENIED", "http_request", null, clock.instant());
            event.withActor(pmsPrincipal.membershipId());
            event.withRequestContext(request.getRemoteAddr(), request.getHeader("User-Agent"));
            auditLogRepository.save(event);
        }
        ApiError body = new ApiError(clock.instant(), 403, "FORBIDDEN", ex.getMessage(), request.getRequestURI(),
                org.slf4j.MDC.get("correlationId"));
        RestAuthenticationEntryPoint.writeJson(response, 403, body);
    }
}
