package com.dawood.releasepilot.deployment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DeploymentRepository extends JpaRepository<Deployment, Long> {

    boolean existsByServiceNameAndVersion(String serviceName, String version);
}
