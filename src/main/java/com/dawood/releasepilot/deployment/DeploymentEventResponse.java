package com.dawood.releasepilot.deployment;

import java.time.Instant;

public record DeploymentEventResponse(
        Long id,
        Long deploymentId,
        DeploymentStatus status,
        String message,
        Instant occurredAt,
        Instant createdAt,
        String provider,
        String externalDeploymentId,
        String commitSha,
        String branchName,
        String triggeredBy,
        String deploymentUrl
) {
}
