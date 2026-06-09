package com.dawood.releasepilot.deployment;

import com.dawood.releasepilot.exception.DeploymentNotFoundException;
import com.dawood.releasepilot.exception.DuplicateDeploymentException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// @Service tells Spring:
// "This class contains business/use-case logic. Create an object of it."
@Service
public class DeploymentService {

    private final DeploymentRepository deploymentRepository;

    // Spring sees this constructor and automatically injects DeploymentRepository.
    public DeploymentService(DeploymentRepository deploymentRepository) {
        this.deploymentRepository = deploymentRepository;
    }

    @Transactional
    public DeploymentResponse createDeployment(CreateDeploymentRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("CreateDeploymentRequest cannot be null");
        }

        boolean alreadyExists = deploymentRepository.existsByServiceNameAndVersion(
                request.serviceName(),
                request.version()
        );

        if (alreadyExists) {
            throw new DuplicateDeploymentException(
                    request.serviceName(),
                    request.version()
            );
        }

        Deployment deployment = new Deployment(
                request.serviceName(),
                request.version()
        );

        Deployment savedDeployment = deploymentRepository.save(deployment);

        return toResponse(savedDeployment);
    }

    @Transactional(readOnly = true)
    public DeploymentResponse getDeployment(Long id) {
        Deployment deployment = findDeploymentOrThrow(id);
        return toResponse(deployment);
    }

    @Transactional(readOnly = true)
    public List<DeploymentResponse> listDeployments() {
        return deploymentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public DeploymentResponse startDeployment(Long id) {
        Deployment deployment = findDeploymentOrThrow(id);

        deployment.start();

        Deployment savedDeployment = deploymentRepository.save(deployment);

        return toResponse(savedDeployment);
    }

    @Transactional
    public DeploymentResponse markSuccessful(Long id) {
        Deployment deployment = findDeploymentOrThrow(id);

        deployment.markSuccessful();

        Deployment savedDeployment = deploymentRepository.save(deployment);

        return toResponse(savedDeployment);
    }

    @Transactional
    public DeploymentResponse markFailed(Long id) {
        Deployment deployment = findDeploymentOrThrow(id);

        deployment.markFailed();

        Deployment savedDeployment = deploymentRepository.save(deployment);

        return toResponse(savedDeployment);
    }

    private Deployment findDeploymentOrThrow(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Deployment ID cannot be null");
        }

        return deploymentRepository.findById(id)
                .orElseThrow(() -> new DeploymentNotFoundException(id));
    }

    private DeploymentResponse toResponse(Deployment deployment) {
        return new DeploymentResponse(
                deployment.getId(),
                deployment.getServiceName(),
                deployment.getVersion(),
                deployment.getStatus(),
                deployment.getCreatedAt(),
                deployment.getStartedAt(),
                deployment.getCompletedAt()
        );
    }
}
