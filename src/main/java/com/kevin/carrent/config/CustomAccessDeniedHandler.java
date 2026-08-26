package com.kevin.carrent.config;

import com.kevin.carrent.dto.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpServletResponse.SC_FORBIDDEN,
                "Access denied",
                LocalDateTime.now(),
                null
        );

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}