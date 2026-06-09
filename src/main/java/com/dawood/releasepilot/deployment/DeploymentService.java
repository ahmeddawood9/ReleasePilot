package com.dawood.releasepilot.deployment;

import com.dawood.releasepilot.exception.DeploymentNotFoundException;
import com.dawood.releasepilot.exception.DuplicateDeploymentException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// @Service tells Spring:
// "This class contains business/use-case logic. Create an object of it."
@Service
public class DeploymentService {

    private final DeploymentRepository deploymentRepository;
    private final DeploymentEventRepository deploymentEventRepository;

    // Spring sees this constructor and automatically injects DeploymentRepository.
    public DeploymentService(
            DeploymentRepository deploymentRepository,
            DeploymentEventRepository deploymentEventRepository
    ) {
        this.deploymentRepository = deploymentRepository;
        this.deploymentEventRepository = deploymentEventRepository;
    }

    @Transactional
    public DeploymentResponse createDeployment(CreateDeploymentRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("CreateDeploymentRequest cannot be null");
        }

        boolean alreadyExists = deploymentRepository.existsByServiceNameAndVersionAndEnvironment(
                request.serviceName(),
                request.version(),
                request.environment()
        );

        if (alreadyExists) {
            throw new DuplicateDeploymentException(
                    request.serviceName(),
                    request.version(),
                    request.environment().name()
            );
        }

        Deployment deployment = new Deployment(
                request.serviceName(),
                request.version(),
                request.environment()
        );

        Deployment savedDeployment = deploymentRepository.save(deployment);
        recordEvent(savedDeployment, DeploymentStatus.PENDING, "Deployment created");

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

    @Transactional(readOnly = true)
    public List<DeploymentEventResponse> listDeploymentEvents(Long deploymentId) {
        findDeploymentOrThrow(deploymentId);

        return deploymentEventRepository.findByDeploymentIdOrderByOccurredAtAsc(deploymentId)
                .stream()
                .map(this::toEventResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<DeploymentResponse> searchDeployments(
            DeploymentStatus status,
            DeploymentEnvironment environment,
            Pageable pageable
    ) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }

        if (status != null && environment != null) {
            return deploymentRepository.findByStatusAndEnvironment(status, environment, pageable)
                    .map(this::toResponse);
        }

        if (status != null) {
            return deploymentRepository.findByStatus(status, pageable)
                    .map(this::toResponse);
        }

        if (environment != null) {
            return deploymentRepository.findByEnvironment(environment, pageable)
                    .map(this::toResponse);
        }

        return deploymentRepository.findAll(pageable)
                .map(this::toResponse);
    }

    @Transactional
    public DeploymentResponse startDeployment(Long id) {
        Deployment deployment = findDeploymentOrThrow(id);

        deployment.start();

        Deployment savedDeployment = deploymentRepository.save(deployment);
        recordEvent(savedDeployment, DeploymentStatus.RUNNING, "Deployment started");

        return toResponse(savedDeployment);
    }

    @Transactional
    public DeploymentResponse markSuccessful(Long id) {
        Deployment deployment = findDeploymentOrThrow(id);

        deployment.markSuccessful();

        Deployment savedDeployment = deploymentRepository.save(deployment);
        recordEvent(savedDeployment, DeploymentStatus.SUCCESS, "Deployment marked successful");

        return toResponse(savedDeployment);
    }

    @Transactional
    public DeploymentResponse markFailed(Long id) {
        Deployment deployment = findDeploymentOrThrow(id);

        deployment.markFailed();

        Deployment savedDeployment = deploymentRepository.save(deployment);
        recordEvent(savedDeployment, DeploymentStatus.FAILED, "Deployment marked failed");

        return toResponse(savedDeployment);
    }

    private void recordEvent(Deployment deployment, DeploymentStatus status, String message) {
        DeploymentEvent event = new DeploymentEvent(deployment, status, message);

        deploymentEventRepository.save(event);
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
                deployment.getEnvironment(),
                deployment.getStatus(),
                deployment.getCreatedAt(),
                deployment.getStartedAt(),
                deployment.getCompletedAt()
        );
    }

    private DeploymentEventResponse toEventResponse(DeploymentEvent event) {
        return new DeploymentEventResponse(
                event.getId(),
                event.getDeployment().getId(),
                event.getStatus(),
                event.getMessage(),
                event.getOccurredAt(),
                event.getCreatedAt(),
                event.getProvider(),
                event.getExternalDeploymentId(),
                event.getCommitSha(),
                event.getBranchName(),
                event.getTriggeredBy(),
                event.getDeploymentUrl()
        );
    }
}
