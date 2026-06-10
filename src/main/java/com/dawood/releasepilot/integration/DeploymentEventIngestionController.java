package com.dawood.releasepilot.integration;

import com.dawood.releasepilot.deployment.DeploymentEventResponse;
import com.dawood.releasepilot.exception.InvalidIntegrationTokenException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/integrations")
public class DeploymentEventIngestionController {

    private final DeploymentEventIngestionService deploymentEventIngestionService;
    private final String ingestionToken;

    public DeploymentEventIngestionController(
            DeploymentEventIngestionService deploymentEventIngestionService,
            @Value("${releasepilot.integrations.ingestion-token}") String ingestionToken
    ) {
        this.deploymentEventIngestionService = deploymentEventIngestionService;
        this.ingestionToken = ingestionToken;
    }

    @PostMapping("/deployment-events")
    public ResponseEntity<DeploymentEventResponse> ingestDeploymentEvent(
            @RequestHeader(name = "X-ReleasePilot-Token", required = false) String token,
            @Valid @RequestBody IngestDeploymentEventRequest request
    ) {
        if (!ingestionToken.equals(token)) {
            throw new InvalidIntegrationTokenException();
        }

        DeploymentEventResponse response = deploymentEventIngestionService.ingestDeploymentEvent(request);

        return ResponseEntity
                .status(202)
                .body(response);
    }
}
