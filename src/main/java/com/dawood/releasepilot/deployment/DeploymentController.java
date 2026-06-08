package com.dawood.releasepilot.deployment;

import org.springframework.web.bind.annotation.*;

import java.util.List;

// @RestController tells Spring:
// "This class handles HTTP API requests."
@RestController

// Base URL for all methods in this controller.
// So every endpoint starts with /api/deployments.
@RequestMapping("/api/deployments")
public class DeploymentController {

    private final DeploymentService deploymentService;

    // Constructor injection.
    // Spring injects DeploymentService here.
    public DeploymentController(DeploymentService deploymentService) {
        this.deploymentService = deploymentService;
    }

    // POST /api/deployments
    //
    // @RequestBody means:
    // Take JSON from HTTP request body
    // and convert it into CreateDeploymentRequest record.
    @PostMapping
    public DeploymentResponse createDeployment(@RequestBody CreateDeploymentRequest request) {
        return deploymentService.createDeployment(request);
    }

    // GET /api/deployments
    //
    // Returns all deployments.
    @GetMapping
    public List<DeploymentResponse> listDeployments() {
        return deploymentService.listDeployments();
    }

    // GET /api/deployments/{id}
    //
    // @PathVariable means:
    // Take {id} from URL and pass it as method parameter.
    @GetMapping("/{id}")
    public DeploymentResponse getDeployment(@PathVariable Long id) {
        return deploymentService.getDeployment(id);
    }

    // PATCH /api/deployments/{id}/start
    //
    // Used when we want to partially update deployment state.
    @PatchMapping("/{id}/start")
    public DeploymentResponse startDeployment(@PathVariable Long id) {
        return deploymentService.startDeployment(id);
    }

    // PATCH /api/deployments/{id}/success
    @PatchMapping("/{id}/success")
    public DeploymentResponse markSuccessful(@PathVariable Long id) {
        return deploymentService.markSuccessful(id);
    }

    // PATCH /api/deployments/{id}/fail
    @PatchMapping("/{id}/fail")
    public DeploymentResponse markFailed(@PathVariable Long id) {
        return deploymentService.markFailed(id);
    }
}