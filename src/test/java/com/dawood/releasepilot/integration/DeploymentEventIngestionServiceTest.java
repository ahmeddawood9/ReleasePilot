package com.dawood.releasepilot.integration;

import com.dawood.releasepilot.deployment.*;
import com.dawood.releasepilot.exception.DeploymentNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DeploymentEventIngestionServiceTest {

    private DeploymentEventIngestionService createService(Deployment deployment) {
        DeploymentRepository deploymentRepository = mock(DeploymentRepository.class);
        DeploymentEventRepository deploymentEventRepository = mock(DeploymentEventRepository.class);
        AtomicLong nextEventId = new AtomicLong(1);

        when(deploymentRepository.findById(deployment.getId())).thenReturn(Optional.of(deployment));

        when(deploymentEventRepository.save(any(DeploymentEvent.class))).thenAnswer(invocation -> {
            DeploymentEvent event = invocation.getArgument(0);

            if (event.getId() == null) {
                ReflectionTestUtils.setField(event, "id", nextEventId.getAndIncrement());
            }

            return event;
        });

        return new DeploymentEventIngestionService(deploymentRepository, deploymentEventRepository);
    }

    @Test
    void shouldIngestExternalDeploymentEvent() {
        Deployment deployment = new Deployment(
                "payment-service",
                "v1.0.0",
                DeploymentEnvironment.DEV
        );
        ReflectionTestUtils.setField(deployment, "id", 1L);

        DeploymentEventIngestionService service = createService(deployment);

        DeploymentEventResponse response = service.ingestDeploymentEvent(
                new IngestDeploymentEventRequest(
                        1L,
                        DeploymentStatus.RUNNING,
                        "GitHub Actions deployment started",
                        "GITHUB_ACTIONS",
                        "gha-123",
                        "abc123",
                        "main",
                        "dawood",
                        "https://github.com/example/actions/runs/123"
                )
        );

        assertEquals(1L, response.deploymentId());
        assertEquals(DeploymentStatus.RUNNING, response.status());
        assertEquals("GitHub Actions deployment started", response.message());
        assertEquals("GITHUB_ACTIONS", response.provider());
        assertEquals("gha-123", response.externalDeploymentId());
        assertEquals("abc123", response.commitSha());
        assertEquals("main", response.branchName());
        assertEquals("dawood", response.triggeredBy());
        assertEquals("https://github.com/example/actions/runs/123", response.deploymentUrl());
        assertNotNull(response.occurredAt());
        assertNotNull(response.createdAt());
    }

    @Test
    void shouldThrowWhenIngestedDeploymentDoesNotExist() {
        DeploymentRepository deploymentRepository = mock(DeploymentRepository.class);
        DeploymentEventRepository deploymentEventRepository = mock(DeploymentEventRepository.class);

        when(deploymentRepository.findById(999L)).thenReturn(Optional.empty());

        DeploymentEventIngestionService service = new DeploymentEventIngestionService(
                deploymentRepository,
                deploymentEventRepository
        );

        assertThrows(
                DeploymentNotFoundException.class,
                () -> service.ingestDeploymentEvent(
                        new IngestDeploymentEventRequest(
                                999L,
                                DeploymentStatus.RUNNING,
                                "Deployment started",
                                "GITHUB_ACTIONS",
                                "gha-123",
                                null,
                                null,
                                null,
                                null
                        )
                )
        );
    }
}
