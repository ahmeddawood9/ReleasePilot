package com.dawood.releasepilot.deployment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// Request DTO = input coming into API.
//
// @NotBlank means:
// field cannot be null
// field cannot be empty
// field cannot be only spaces
public record CreateDeploymentRequest(
        @NotBlank(message = "Service name is required")
        String serviceName,

        @NotBlank(message = "Version is required")
        String version,

        @NotNull(message = "Environment is required")
        DeploymentEnvironment environment
) {
}
