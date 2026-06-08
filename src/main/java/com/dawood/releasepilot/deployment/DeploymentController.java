package com.dawood.releasepilot.deployment;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deployments")
public class DeploymentController {

    private final DeploymentService deploymentService;

    public DeploymentController(DeploymentService deploymentService) {
        this.deploymentService = deploymentService;
    }

    @PostMapping
    public DeploymentResponse createDeployment(@Valid @RequestBody CreateDeploymentRequest request) {
        return deploymentService.createDeployment(request);
    }

    @GetMapping
    public List<DeploymentResponse> listDeployments() {
        return deploymentService.listDeployments();
    }

    @GetMapping("/{id}")
    public DeploymentResponse getDeployment(@PathVariable Long id) {
        return deploymentService.getDeployment(id);
    }

    @PatchMapping("/{id}/start")
    public DeploymentResponse startDeployment(@PathVariable Long id) {
        return deploymentService.startDeployment(id);
    }

    @PatchMapping("/{id}/success")
    public DeploymentResponse markSuccessful(@PathVariable Long id) {
        return deploymentService.markSuccessful(id);
    }

    @PatchMapping("/{id}/fail")
    public DeploymentResponse markFailed(@PathVariable Long id) {
        return deploymentService.markFailed(id);
    }
}