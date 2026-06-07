package com.dawood.releasepilot;

import com.dawood.releasepilot.deployment.DeploymentRepository;
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
    }
}
