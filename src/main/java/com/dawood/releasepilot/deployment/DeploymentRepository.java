package com.dawood.releasepilot.deployment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeploymentRepository extends JpaRepository<Deployment, Long> {

    boolean existsByServiceNameAndVersionAndEnvironment(
            String serviceName,
            String version,
            DeploymentEnvironment environment
    );

    Page<Deployment> findByStatus(DeploymentStatus status, Pageable pageable);

    Page<Deployment> findByEnvironment(DeploymentEnvironment environment, Pageable pageable);

    Page<Deployment> findByStatusAndEnvironment(
            DeploymentStatus status,
            DeploymentEnvironment environment,
            Pageable pageable
    );

    long countByStatus(DeploymentStatus status);

    long countByEnvironment(DeploymentEnvironment environment);
}
