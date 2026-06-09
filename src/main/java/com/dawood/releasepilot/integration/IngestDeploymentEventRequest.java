package com.dawood.releasepilot.integration;

import com.dawood.releasepilot.deployment.DeploymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IngestDeploymentEventRequest(
        @NotNull(message = "Deployment ID is required")
        Long deploymentId,

        @NotNull(message = "Status is required")
        DeploymentStatus status,

        @NotBlank(message = "Message is required")
        String message,

        @NotBlank(message = "Provider is required")
        String provider,

        @NotBlank(message = "External deployment ID is required")
        String externalDeploymentId,

        String commitSha,
        String branchName,
        String triggeredBy,
        String deploymentUrl
) {
}
