package com.dawood.releasepilot.deployment;

import com.dawood.releasepilot.exception.DeploymentNotFoundException;
import com.dawood.releasepilot.exception.InvalidDeploymentStateException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// This class tests DeploymentService.
// We are testing backend behavior, not just syntax.
class DeploymentServiceTest {

    // Helper method to create a fresh service for every test.
    // Each test gets its own fake repository, so tests do not affect each other.
    private DeploymentService createService() {
        DeploymentRepository repository = new InMemoryDeploymentRepository();
        return new DeploymentService(repository);
    }

    @Test
    void shouldCreateDeploymentWithPendingStatus() {
        // Arrange: prepare service and request.
        DeploymentService service = createService();

        CreateDeploymentRequest request = new CreateDeploymentRequest(
                "payment-service",
                "v1.0.0"
        );

        // Act: call the method we want to test.
        DeploymentResponse response = service.createDeployment(request);

        // Assert: verify expected result.
        assertNotNull(response.id());
        assertEquals("payment-service", response.serviceName());
        assertEquals("v1.0.0", response.version());
        assertEquals(DeploymentStatus.PENDING, response.status());

        // Timestamp checks.
        assertNotNull(response.createdAt());
        assertNull(response.startedAt());
        assertNull(response.completedAt());
    }

    @Test
    void shouldListDeployments() {
        DeploymentService service = createService();

        service.createDeployment(new CreateDeploymentRequest("payment-service", "v1.0.0"));
        service.createDeployment(new CreateDeploymentRequest("user-service", "v2.0.0"));

        List<DeploymentResponse> deployments = service.listDeployments();

        assertEquals(2, deployments.size());
    }

    @Test
    void shouldStartPendingDeployment() {
        DeploymentService service = createService();

        DeploymentResponse created = service.createDeployment(
                new CreateDeploymentRequest("payment-service", "v1.0.0")
        );

        DeploymentResponse started = service.startDeployment(created.id());

        assertEquals(DeploymentStatus.RUNNING, started.status());
        assertNotNull(started.startedAt());
        assertNull(started.completedAt());
    }

    @Test
    void shouldMarkRunningDeploymentSuccessful() {
        DeploymentService service = createService();

        DeploymentResponse created = service.createDeployment(
                new CreateDeploymentRequest("payment-service", "v1.0.0")
        );

        service.startDeployment(created.id());

        DeploymentResponse successful = service.markSuccessful(created.id());

        assertEquals(DeploymentStatus.SUCCESS, successful.status());
        assertNotNull(successful.completedAt());
    }

    @Test
    void shouldMarkRunningDeploymentFailed() {
        DeploymentService service = createService();

        DeploymentResponse created = service.createDeployment(
                new CreateDeploymentRequest("payment-service", "v1.0.0")
        );

        service.startDeployment(created.id());

        DeploymentResponse failed = service.markFailed(created.id());

        assertEquals(DeploymentStatus.FAILED, failed.status());
        assertNotNull(failed.completedAt());
    }

    @Test
    void shouldThrowWhenDeploymentNotFound() {
        DeploymentService service = createService();

        assertThrows(
                DeploymentNotFoundException.class,
                () -> service.getDeployment(999L)
        );
    }

    @Test
    void shouldNotAllowPendingDeploymentToBecomeSuccessfulDirectly() {
        DeploymentService service = createService();

        DeploymentResponse created = service.createDeployment(
                new CreateDeploymentRequest("payment-service", "v1.0.0")
        );

        assertThrows(
                InvalidDeploymentStateException.class,
                () -> service.markSuccessful(created.id())
        );
    }

    @Test
    void shouldNotAllowSuccessfulDeploymentToBecomeFailed() {
        DeploymentService service = createService();

        DeploymentResponse created = service.createDeployment(
                new CreateDeploymentRequest("payment-service", "v1.0.0")
        );

        service.startDeployment(created.id());
        service.markSuccessful(created.id());

        assertThrows(
                InvalidDeploymentStateException.class,
                () -> service.markFailed(created.id())
        );
    }
}
