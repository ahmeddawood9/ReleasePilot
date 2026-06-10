package com.dawood.releasepilot.config;

import com.dawood.releasepilot.exception.InvalidIntegrationTokenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class IntegrationTokenInterceptor implements HandlerInterceptor {

    private static final String TOKEN_HEADER = "X-ReleasePilot-Token";

    private final String ingestionToken;

    public IntegrationTokenInterceptor(
            @Value("${releasepilot.integrations.ingestion-token}") String ingestionToken
    ) {
        this.ingestionToken = ingestionToken;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {
        String token = request.getHeader(TOKEN_HEADER);

        if (!ingestionToken.equals(token)) {
            throw new InvalidIntegrationTokenException();
        }

        return true;
    }
}
