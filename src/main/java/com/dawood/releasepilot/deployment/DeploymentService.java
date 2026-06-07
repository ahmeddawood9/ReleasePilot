package com.dawood.releasepilot.deployment;

import com.dawood.releasepilot.exception.DeploymentNotFoundException;

import java.util.List;

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

    // Use case 2:
    // Get one deployment by ID.
    public DeploymentResponse getDeployment(Long id) {
        Deployment deployment = findDeploymentOrThrow(id);

        return toResponse(deployment);
    }

    // Use case 3:
    // List all deployments.
    public List<DeploymentResponse> listDeployments() {
        // findAll() returns List<Deployment>
        // stream() processes that list
        // map(this::toResponse) converts Deployment -> DeploymentResponse
        // toList() returns List<DeploymentResponse>
        return deploymentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Private helper method.
    // It avoids repeating findById + orElseThrow in every service method.
    private Deployment findDeploymentOrThrow(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Deployment ID cannot be null");
        }

        return deploymentRepository.findById(id)
                .orElseThrow(() -> new DeploymentNotFoundException(id));
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
