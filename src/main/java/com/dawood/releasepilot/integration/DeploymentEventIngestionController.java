package com.dawood.releasepilot.integration;

import com.dawood.releasepilot.deployment.DeploymentEventResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/integrations")
public class DeploymentEventIngestionController {

    private final DeploymentEventIngestionService deploymentEventIngestionService;

    public DeploymentEventIngestionController(
            DeploymentEventIngestionService deploymentEventIngestionService
    ) {
        this.deploymentEventIngestionService = deploymentEventIngestionService;
    }

    @PostMapping("/deployment-events")
    public ResponseEntity<DeploymentEventResponse> ingestDeploymentEvent(
            @Valid @RequestBody IngestDeploymentEventRequest request
    ) {
        DeploymentEventResponse response = deploymentEventIngestionService.ingestDeploymentEvent(request);

        return ResponseEntity
                .status(202)
                .body(response);
    }
}
