package com.dawood.releasepilot.deployment;

// Response DTO = clean output returned from service.
// We do not return Deployment directly because Deployment is internal domain object.
// Response should expose only the fields we want outside code/API user to see.
public record DeploymentResponse(
    Long id,
    String serviceName,
    String version,
    DeploymentStatus status
) {
    
}