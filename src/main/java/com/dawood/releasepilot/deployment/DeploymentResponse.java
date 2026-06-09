package com.dawood.releasepilot.deployment;

import java.time.Instant;

// Response DTO = clean output returned from service.
public record DeploymentResponse(
        Long id,
        String serviceName,
        String version,
        DeploymentEnvironment environment,
        DeploymentStatus status,
        Instant createdAt,
        Instant startedAt,
        Instant completedAt
) {
}
