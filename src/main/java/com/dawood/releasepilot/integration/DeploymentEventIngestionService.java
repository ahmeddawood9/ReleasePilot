package com.dawood.releasepilot.integration;

import com.dawood.releasepilot.deployment.Deployment;
import com.dawood.releasepilot.deployment.DeploymentEvent;
import com.dawood.releasepilot.deployment.DeploymentEventRepository;
import com.dawood.releasepilot.deployment.DeploymentEventResponse;
import com.dawood.releasepilot.deployment.DeploymentRepository;
import com.dawood.releasepilot.exception.DeploymentNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeploymentEventIngestionService {

    private final DeploymentRepository deploymentRepository;
    private final DeploymentEventRepository deploymentEventRepository;

    public DeploymentEventIngestionService(
            DeploymentRepository deploymentRepository,
            DeploymentEventRepository deploymentEventRepository
    ) {
        this.deploymentRepository = deploymentRepository;
        this.deploymentEventRepository = deploymentEventRepository;
    }

    @Transactional
    public DeploymentEventResponse ingestDeploymentEvent(IngestDeploymentEventRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("IngestDeploymentEventRequest cannot be null");
        }

        Deployment deployment = deploymentRepository.findById(request.deploymentId())
                .orElseThrow(() -> new DeploymentNotFoundException(request.deploymentId()));

        DeploymentEvent event = new DeploymentEvent(
                deployment,
                request.status(),
                request.message(),
                request.provider(),
                request.externalDeploymentId(),
                request.commitSha(),
                request.branchName(),
                request.triggeredBy(),
                request.deploymentUrl()
        );

        DeploymentEvent savedEvent = deploymentEventRepository.save(event);

        return toResponse(savedEvent);
    }

    private DeploymentEventResponse toResponse(DeploymentEvent event) {
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
