package com.dawood.releasepilot;

import com.dawood.releasepilot.deployment.CreateDeploymentRequest;
import com.dawood.releasepilot.deployment.DeploymentRepository;
import com.dawood.releasepilot.deployment.DeploymentResponse;
import com.dawood.releasepilot.deployment.DeploymentService;
import com.dawood.releasepilot.deployment.InMemoryDeploymentRepository;

// Main is only for manual testing right now.
// Later, Spring Boot Controller will replace this manual flow.
public class Main {
    public static void main(String[] args) {
        // Create repository.
        // This is our fake database.
        DeploymentRepository repository = new InMemoryDeploymentRepository();

        // Create service and inject repository into it.
        DeploymentService service = new DeploymentService(repository);

        // Create request DTO.
        CreateDeploymentRequest request = new CreateDeploymentRequest(
                "payment-service",
                "v1.0.0"
        );

        // Create deployment.
        DeploymentResponse createdDeployment = service.createDeployment(request);
        System.out.println("Created:");
        System.out.println(createdDeployment);

        // Start deployment.
        DeploymentResponse runningDeployment = service.startDeployment(createdDeployment.id());
        System.out.println("\nStarted:");
        System.out.println(runningDeployment);

        // Mark deployment successful.
        DeploymentResponse successfulDeployment = service.markSuccessful(createdDeployment.id());
        System.out.println("\nSuccessful:");
        System.out.println(successfulDeployment);
    }
}
