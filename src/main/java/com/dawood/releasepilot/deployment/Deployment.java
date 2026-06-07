package com.dawood.releasepilot.deployment;

import com.dawood.releasepilot.exception.InvalidDeploymentStateException;

import java.time.Instant;

// Domain class = real business object with rules.
public class Deployment {

    private Long id;
    private String serviceName;
    private String version;
    private DeploymentStatus status;

    // Instant represents a precise timestamp in UTC.
    // Good for backend systems, logs, deployments, audit records.
    private final Instant createdAt;
    private Instant startedAt;
    private Instant completedAt;

    public Deployment(Long id, String serviceName, String version) {
        if (serviceName == null || serviceName.isBlank()) {
            throw new IllegalArgumentException("Service name is required");
        }

        if (version == null || version.isBlank()) {
            throw new IllegalArgumentException("Version is required");
        }

        this.id = id;
        this.serviceName = serviceName;
        this.version = version;
        this.status = DeploymentStatus.PENDING;

        // When object is created, store creation time.
        this.createdAt = Instant.now();
    }

    public void assignId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        if (this.id != null) {
            throw new IllegalStateException("ID is already assigned");
        }

        this.id = id;
    }

    public void start() {
        if (status != DeploymentStatus.PENDING) {
            throw new InvalidDeploymentStateException(
                    "Only PENDING deployment can be started. Current status: " + status
            );
        }

        status = DeploymentStatus.RUNNING;

        // Store the time when deployment started.
        startedAt = Instant.now();
    }

    public void markSuccessful() {
        if (status != DeploymentStatus.RUNNING) {
            throw new InvalidDeploymentStateException(
                    "Only RUNNING deployment can be marked successful. Current status: " + status
            );
        }

        status = DeploymentStatus.SUCCESS;

        // Store the time when deployment completed.
        completedAt = Instant.now();
    }

    public void markFailed() {
        if (status != DeploymentStatus.RUNNING) {
            throw new InvalidDeploymentStateException(
                    "Only RUNNING deployment can be marked failed. Current status: " + status
            );
        }

        status = DeploymentStatus.FAILED;

        // Store the time when deployment completed.
        completedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getVersion() {
        return version;
    }

    public DeploymentStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }
}