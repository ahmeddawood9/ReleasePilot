package com.dawood.releasepilot;

import com.dawood.releasepilot.deployment.CreateDeploymentRequest;
import com.dawood.releasepilot.deployment.DeploymentRepository;
import com.dawood.releasepilot.deployment.DeploymentResponse;
import com.dawood.releasepilot.deployment.DeploymentService;
import com.dawood.releasepilot.deployment.InMemoryDeploymentRepository;
import com.dawood.releasepilot.exception.InvalidDeploymentStateException;

import java.util.List;

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

        // List all deployments.
        List<DeploymentResponse> deployments = service.listDeployments();
        System.out.println("\nAll deployments:");
        deployments.forEach(System.out::println);

        // Try invalid transition:
        // SUCCESS deployment cannot become FAILED.
        try {
            service.markFailed(createdDeployment.id());
        } catch (InvalidDeploymentStateException ex) {
            System.out.println("\nInvalid transition caught:");
            System.out.println(ex.getMessage());
        }
    }
}
