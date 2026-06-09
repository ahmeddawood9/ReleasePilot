package com.dawood.releasepilot.deployment;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<DeploymentResponse> createDeployment(
            @Valid @RequestBody CreateDeploymentRequest request
    ) {
        DeploymentResponse response = deploymentService.createDeployment(request);

        return ResponseEntity
                .status(201)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<Page<DeploymentResponse>> listDeployments(
            @RequestParam(required = false) DeploymentStatus status,
            @RequestParam(required = false) DeploymentEnvironment environment,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<DeploymentResponse> response = deploymentService.searchDeployments(
                status,
                environment,
                pageRequest
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeploymentResponse> getDeployment(@PathVariable Long id) {
        DeploymentResponse response = deploymentService.getDeployment(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/events")
    public ResponseEntity<List<DeploymentEventResponse>> listDeploymentEvents(@PathVariable Long id) {
        List<DeploymentEventResponse> response = deploymentService.listDeploymentEvents(id);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/start")
    public ResponseEntity<DeploymentResponse> startDeployment(@PathVariable Long id) {
        DeploymentResponse response = deploymentService.startDeployment(id);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/success")
    public ResponseEntity<DeploymentResponse> markSuccessful(@PathVariable Long id) {
        DeploymentResponse response = deploymentService.markSuccessful(id);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/fail")
    public ResponseEntity<DeploymentResponse> markFailed(@PathVariable Long id) {
        DeploymentResponse response = deploymentService.markFailed(id);

        return ResponseEntity.ok(response);
    }
}
