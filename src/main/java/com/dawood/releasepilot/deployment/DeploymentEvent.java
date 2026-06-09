package com.dawood.releasepilot.deployment;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "deployment_events")
public class DeploymentEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "deployment_id", nullable = false)
    private Deployment deployment;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DeploymentStatus status;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "provider")
    private String provider;

    @Column(name = "external_deployment_id")
    private String externalDeploymentId;

    @Column(name = "commit_sha")
    private String commitSha;

    @Column(name = "branch_name")
    private String branchName;

    @Column(name = "triggered_by")
    private String triggeredBy;

    @Column(name = "deployment_url")
    private String deploymentUrl;

    protected DeploymentEvent() {
    }

    public DeploymentEvent(Deployment deployment, DeploymentStatus status, String message) {
        if (deployment == null) {
            throw new IllegalArgumentException("Deployment is required");
        }

        if (status == null) {
            throw new IllegalArgumentException("Status is required");
        }

        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message is required");
        }

        Instant now = Instant.now();

        this.deployment = deployment;
        this.status = status;
        this.message = message;
        this.occurredAt = now;
        this.createdAt = now;
    }

    public DeploymentEvent(
            Deployment deployment,
            DeploymentStatus status,
            String message,
            String provider,
            String externalDeploymentId,
            String commitSha,
            String branchName,
            String triggeredBy,
            String deploymentUrl
    ) {
        this(deployment, status, message);

        this.provider = provider;
        this.externalDeploymentId = externalDeploymentId;
        this.commitSha = commitSha;
        this.branchName = branchName;
        this.triggeredBy = triggeredBy;
        this.deploymentUrl = deploymentUrl;
    }

    public Long getId() {
        return id;
    }

    public Deployment getDeployment() {
        return deployment;
    }

    public DeploymentStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getProvider() {
        return provider;
    }

    public String getExternalDeploymentId() {
        return externalDeploymentId;
    }

    public String getCommitSha() {
        return commitSha;
    }

    public String getBranchName() {
        return branchName;
    }

    public String getTriggeredBy() {
        return triggeredBy;
    }

    public String getDeploymentUrl() {
        return deploymentUrl;
    }
}
