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

    // Use case 1:
    // Create a new deployment.
    public DeploymentResponse createDeployment(CreateDeploymentRequest request) {
        // Validate request object itself.
        if (request == null) {
            throw new IllegalArgumentException("CreateDeploymentRequest cannot be null");
        }

        // Create domain object from request DTO.
        Deployment deployment = new Deployment(
                null,
                request.serviceName(),
                request.version()
        );

        // Save using repository.
        Deployment savedDeployment = deploymentRepository.save(deployment);

        // Convert internal domain object into response DTO.
        return toResponse(savedDeployment);
    }

    // Private mapper method.
    // Converts internal Deployment object into DeploymentResponse DTO.
    private DeploymentResponse toResponse(Deployment deployment) {
        return new DeploymentResponse(
                deployment.getId(),
                deployment.getServiceName(),
                deployment.getVersion(),
                deployment.getStatus()
        );
    }
}
