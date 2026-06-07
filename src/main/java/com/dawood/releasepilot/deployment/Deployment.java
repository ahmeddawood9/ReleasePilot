package com.dawood.releasepilot.deployment ;
import com.dawood.releasepilot.exception.InvalidDeploymentStateException ;

// domain class = real buisness rules .

public class Deployment {

    private Long id;
    private String serviceName;
    private String version;
    private DeploymentStatus status;

    // ID can be null at first , becauase repository will assign it later.

    public Deployment(Long id ,String serviceName,String version) {

        if(serviceName == null || serviceName.isBlank()) {
            throw new IllegalArgumentException("Service name is required");
        }

        if(version == null || version.isBlank()) {
            throw new IllegalArgumentException("version is required");
        }

        this.id = id;
        this.serviceName = serviceName;
        this.version = version;

        this.status = DeploymentStatus.PENDING; // every new deployment starts as PENDING

    }


    //repository will call this method when saving a new deployment.
    public void assignId(Long id ) {

        //making sure ID is not null
        if(id == null) {
            throw new IllegalArgumentException("ID cannnot be null");
        }

        //if ID is already assigned, do not allow chaning it 
        if(this.id != null) {
            throw new IllegalStateException("ID is already assigned");
        }

        this.id = id;
    }

    //business rule : 
    // Only PENDING deployment can start.
    public void start() {
        if(status != DeploymentStatus.PENDING) {
            throw new InvalidDeploymentStateException("Only PENDING deployment can be started. Current status: " + status);
        }
        status = DeploymentStatus.RUNNING;
    }

    //only RUNNING deploymet can become SUCCESS.
    public void markSuccessful() {
        if(status != DeploymentStatus.RUNNING) {
            throw new InvalidDeploymentStateException( "Only RUNNING deployment can be marked successful. Current status: " + status);
        }
        status = DeploymentStatus.SUCCESS;
    }

    //only running deployment can become failed
    public void markFailed() {
        if(status != DeploymentStatus.RUNNING) {
            throw new InvalidDeploymentStateException("Only RUNNING deployment can be marked failed. Current status: " + status);
        }
        status = DeploymentStatus.FAILED;
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
}
