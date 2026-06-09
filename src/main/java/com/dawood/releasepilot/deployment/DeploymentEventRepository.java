package com.dawood.releasepilot.deployment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeploymentEventRepository extends JpaRepository<DeploymentEvent, Long> {

    List<DeploymentEvent> findByDeploymentIdOrderByOccurredAtAsc(Long deploymentId);
}
