package com.jlmasa.pms_backend.security;

import com.jlmasa.pms_backend.common.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.time.Clock;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // Jackson 3 (Spring Boot 4): java.time types are supported natively and written as ISO-8601 strings.
    private static final ObjectMapper MAPPER = JsonMapper.builder().build();
    private final Clock clock;

    public RestAuthenticationEntryPoint(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex)
            throws java.io.IOException {
        ApiError body = new ApiError(clock.instant(), 401, "UNAUTHENTICATED", "Authentication is required.",
                request.getRequestURI(), org.slf4j.MDC.get("correlationId"));
        writeJson(response, 401, body);
    }

    static void writeJson(HttpServletResponse response, int status, Object body) throws java.io.IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        MAPPER.writeValue(response.getWriter(), body);
    }
}