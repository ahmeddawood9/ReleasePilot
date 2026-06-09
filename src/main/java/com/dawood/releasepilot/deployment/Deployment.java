package com.dawood.releasepilot.deployment;

import com.dawood.releasepilot.exception.InvalidDeploymentStateException;
import jakarta.persistence.*;

import java.time.Instant;

// @Entity tells JPA:
// This Java class should be stored in a database table.
@Entity

// Table name in PostgreSQL.
@Table(
        name = "deployments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_deployment_service_version",
                        columnNames = {"service_name", "version", "environment"}
                )
        }
)
public class Deployment {

    // @Id marks primary key.
    // @GeneratedValue means database/JPA generates ID.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // nullable=false means this column cannot be null.
    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Column(name = "version", nullable = false)
    private String version;

    @Enumerated(EnumType.STRING)
    @Column(name = "environment", nullable = false, columnDefinition = "varchar(255) default 'DEV'")
    private DeploymentEnvironment environment;

    // EnumType.STRING stores enum as readable text:
    // PENDING, RUNNING, SUCCESS, FAILED
    //
    // Do not use ORDINAL for backend enums.
    // ORDINAL stores 0,1,2,3 and becomes dangerous if enum order changes.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeploymentStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant startedAt;

    private Instant completedAt;

    // JPA requires a no-argument constructor.
    // protected means normal app code should not use it directly.
    protected Deployment() {
    }

    // App code uses this constructor.
    public Deployment(String serviceName, String version, DeploymentEnvironment environment) {
        if (serviceName == null || serviceName.isBlank()) {
            throw new IllegalArgumentException("Service name is required");
        }

        if (version == null || version.isBlank()) {
            throw new IllegalArgumentException("Version is required");
        }

        if (environment == null) {
            throw new IllegalArgumentException("Environment is required");
        }

        this.serviceName = serviceName;
        this.version = version;
        this.environment = environment;
        this.status = DeploymentStatus.PENDING;
        this.createdAt = Instant.now();
    }

    public void start() {
        if (status != DeploymentStatus.PENDING) {
            throw new InvalidDeploymentStateException(
                    "Only PENDING deployment can be started. Current status: " + status
            );
        }

        status = DeploymentStatus.RUNNING;
        startedAt = Instant.now();
    }

    public void markSuccessful() {
        if (status != DeploymentStatus.RUNNING) {
            throw new InvalidDeploymentStateException(
                    "Only RUNNING deployment can be marked successful. Current status: " + status
            );
        }

        status = DeploymentStatus.SUCCESS;
        completedAt = Instant.now();
    }

    public void markFailed() {
        if (status != DeploymentStatus.RUNNING) {
            throw new InvalidDeploymentStateException(
                    "Only RUNNING deployment can be marked failed. Current status: " + status
            );
        }

        status = DeploymentStatus.FAILED;
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

    public DeploymentEnvironment getEnvironment() {
        return environment;
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
