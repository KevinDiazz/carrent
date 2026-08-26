package com.kevin.carrent.config;

import com.kevin.carrent.dto.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            @NonNull HttpServletRequest request,
            HttpServletResponse response,
            @NonNull AuthenticationException authException)
            throws IOException, ServletException {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Authentication required",
                LocalDateTime.now(),
                null
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}