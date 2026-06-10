package com.dawood.releasepilot.deployment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeploymentEventRepository extends JpaRepository<DeploymentEvent, Long> {

    List<DeploymentEvent> findByDeploymentIdOrderByOccurredAtAsc(Long deploymentId);

    Optional<DeploymentEvent> findByProviderAndExternalDeploymentIdAndStatus(
            String provider,
            String externalDeploymentId,
            DeploymentStatus status
    );
}
