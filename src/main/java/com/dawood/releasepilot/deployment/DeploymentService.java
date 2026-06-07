package com.dawood.releasepilot.deployment;

// Service layer = main backend logic layer.
// It coordinates use cases.
//
// Service uses:
// - DTOs
// - domain objects
// - repository
// - exceptions
public class DeploymentService {
    private final DeploymentRepository deploymentRepository;

    public DeploymentService(DeploymentRepository deploymentRepository) {
        this.deploymentRepository = deploymentRepository;
    }
}
