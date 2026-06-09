package com.dawood.releasepilot.deployment;

import com.dawood.releasepilot.exception.DeploymentNotFoundException;
import com.dawood.releasepilot.exception.DuplicateDeploymentException;
import com.dawood.releasepilot.exception.InvalidDeploymentStateException;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// This class tests DeploymentService.
// We are testing backend behavior, not just syntax.
class DeploymentServiceTest {

    // Helper method to create a fresh service for every test.
    // Each test gets its own fake repository, so tests do not affect each other.
    private DeploymentService createService() {
        DeploymentRepository repository = mock(DeploymentRepository.class);
        Map<Long, Deployment> deployments = new LinkedHashMap<>();
        AtomicLong nextId = new AtomicLong(1);

        when(repository.save(any(Deployment.class))).thenAnswer(invocation -> {
            Deployment deployment = invocation.getArgument(0);

            if (deployment.getId() == null) {
                ReflectionTestUtils.setField(deployment, "id", nextId.getAndIncrement());
            }

            deployments.put(deployment.getId(), deployment);
            return deployment;
        });

        when(repository.findById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return Optional.ofNullable(deployments.get(id));
        });

        when(repository.existsByServiceNameAndVersionAndEnvironment(any(), any(), any())).thenAnswer(invocation -> {
            String serviceName = invocation.getArgument(0);
            String version = invocation.getArgument(1);
            DeploymentEnvironment environment = invocation.getArgument(2);

            return deployments.values()
                    .stream()
                    .anyMatch(deployment ->
                            deployment.getServiceName().equals(serviceName)
                                    && deployment.getVersion().equals(version)
                                    && deployment.getEnvironment() == environment
                    );
        });

        when(repository.findAll()).thenAnswer(invocation -> List.copyOf(deployments.values()));

        when(repository.findAll(any(Pageable.class))).thenAnswer(invocation -> {
            Pageable pageable = invocation.getArgument(0);
            return toPage(List.copyOf(deployments.values()), pageable);
        });

        when(repository.findByStatus(any(), any(Pageable.class))).thenAnswer(invocation -> {
            DeploymentStatus status = invocation.getArgument(0);
            Pageable pageable = invocation.getArgument(1);

            List<Deployment> filteredDeployments = deployments.values()
                    .stream()
                    .filter(deployment -> deployment.getStatus() == status)
                    .toList();

            return toPage(filteredDeployments, pageable);
        });

        when(repository.findByEnvironment(any(), any(Pageable.class))).thenAnswer(invocation -> {
            DeploymentEnvironment environment = invocation.getArgument(0);
            Pageable pageable = invocation.getArgument(1);

            List<Deployment> filteredDeployments = deployments.values()
                    .stream()
                    .filter(deployment -> deployment.getEnvironment() == environment)
                    .toList();

            return toPage(filteredDeployments, pageable);
        });

        when(repository.findByStatusAndEnvironment(any(), any(), any(Pageable.class))).thenAnswer(invocation -> {
            DeploymentStatus status = invocation.getArgument(0);
            DeploymentEnvironment environment = invocation.getArgument(1);
            Pageable pageable = invocation.getArgument(2);

            List<Deployment> filteredDeployments = deployments.values()
                    .stream()
                    .filter(deployment ->
                            deployment.getStatus() == status
                                    && deployment.getEnvironment() == environment
                    )
                    .toList();

            return toPage(filteredDeployments, pageable);
        });

        return new DeploymentService(repository);
    }

    private Page<Deployment> toPage(List<Deployment> deployments, Pageable pageable) {
        int start = Math.toIntExact(pageable.getOffset());
        int end = Math.min(start + pageable.getPageSize(), deployments.size());
        List<Deployment> pageContent = start >= deployments.size()
                ? List.of()
                : deployments.subList(start, end);

        return new PageImpl<>(pageContent, pageable, deployments.size());
    }

    @Test
    void shouldCreateDeploymentWithPendingStatus() {
        // Arrange: prepare service and request.
        DeploymentService service = createService();

        CreateDeploymentRequest request = new CreateDeploymentRequest(
                "payment-service",
                "v1.0.0",
                DeploymentEnvironment.DEV
        );

        // Act: call the method we want to test.
        DeploymentResponse response = service.createDeployment(request);

        // Assert: verify expected result.
        assertNotNull(response.id());
        assertEquals("payment-service", response.serviceName());
        assertEquals("v1.0.0", response.version());
        assertEquals(DeploymentEnvironment.DEV, response.environment());
        assertEquals(DeploymentStatus.PENDING, response.status());

        // Timestamp checks.
        assertNotNull(response.createdAt());
        assertNull(response.startedAt());
        assertNull(response.completedAt());
    }

    @Test
    void shouldListDeployments() {
        DeploymentService service = createService();

        service.createDeployment(new CreateDeploymentRequest(
                "payment-service",
                "v1.0.0",
                DeploymentEnvironment.DEV
        ));
        service.createDeployment(new CreateDeploymentRequest(
                "user-service",
                "v2.0.0",
                DeploymentEnvironment.STAGING
        ));

        List<DeploymentResponse> deployments = service.listDeployments();

        assertEquals(2, deployments.size());
    }

    @Test
    void shouldSearchDeploymentsByStatus() {
        DeploymentService service = createService();

        DeploymentResponse runningDeployment = service.createDeployment(new CreateDeploymentRequest(
                "payment-service",
                "v1.0.0",
                DeploymentEnvironment.DEV
        ));
        service.startDeployment(runningDeployment.id());

        service.createDeployment(new CreateDeploymentRequest(
                "user-service",
                "v2.0.0",
                DeploymentEnvironment.DEV
        ));

        Page<DeploymentResponse> deployments = service.searchDeployments(
                DeploymentStatus.RUNNING,
                null,
                PageRequest.of(0, 10)
        );

        assertEquals(1, deployments.getTotalElements());
        assertEquals(DeploymentStatus.RUNNING, deployments.getContent().getFirst().status());
    }

    @Test
    void shouldSearchDeploymentsByEnvironment() {
        DeploymentService service = createService();

        service.createDeployment(new CreateDeploymentRequest(
                "payment-service",
                "v1.0.0",
                DeploymentEnvironment.DEV
        ));
        service.createDeployment(new CreateDeploymentRequest(
                "user-service",
                "v2.0.0",
                DeploymentEnvironment.PRODUCTION
        ));

        Page<DeploymentResponse> deployments = service.searchDeployments(
                null,
                DeploymentEnvironment.PRODUCTION,
                PageRequest.of(0, 10)
        );

        assertEquals(1, deployments.getTotalElements());
        assertEquals(DeploymentEnvironment.PRODUCTION, deployments.getContent().getFirst().environment());
    }

    @Test
    void shouldPaginateDeployments() {
        DeploymentService service = createService();

        service.createDeployment(new CreateDeploymentRequest(
                "payment-service",
                "v1.0.0",
                DeploymentEnvironment.DEV
        ));
        service.createDeployment(new CreateDeploymentRequest(
                "user-service",
                "v2.0.0",
                DeploymentEnvironment.DEV
        ));

        Page<DeploymentResponse> deployments = service.searchDeployments(
                null,
                null,
                PageRequest.of(0, 1)
        );

        assertEquals(2, deployments.getTotalElements());
        assertEquals(1, deployments.getSize());
        assertEquals(1, deployments.getContent().size());
    }

    @Test
    void shouldRejectDuplicateDeployment() {
        DeploymentService service = createService();

        service.createDeployment(
                new CreateDeploymentRequest(
                        "payment-service",
                        "v1.0.0",
                        DeploymentEnvironment.DEV
                )
        );

        assertThrows(
                DuplicateDeploymentException.class,
                () -> service.createDeployment(
                        new CreateDeploymentRequest(
                                "payment-service",
                                "v1.0.0",
                                DeploymentEnvironment.DEV
                        )
                )
        );
    }

    @Test
    void shouldAllowSameServiceAndVersionInDifferentEnvironment() {
        DeploymentService service = createService();

        service.createDeployment(
                new CreateDeploymentRequest(
                        "payment-service",
                        "v1.0.0",
                        DeploymentEnvironment.DEV
                )
        );

        DeploymentResponse stagingDeployment = service.createDeployment(
                new CreateDeploymentRequest(
                        "payment-service",
                        "v1.0.0",
                        DeploymentEnvironment.STAGING
                )
        );

        assertEquals(DeploymentEnvironment.STAGING, stagingDeployment.environment());
    }

    @Test
    void shouldStartPendingDeployment() {
        DeploymentService service = createService();

        DeploymentResponse created = service.createDeployment(
                new CreateDeploymentRequest(
                        "payment-service",
                        "v1.0.0",
                        DeploymentEnvironment.DEV
                )
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
                new CreateDeploymentRequest(
                        "payment-service",
                        "v1.0.0",
                        DeploymentEnvironment.DEV
                )
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
                new CreateDeploymentRequest(
                        "payment-service",
                        "v1.0.0",
                        DeploymentEnvironment.DEV
                )
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
                new CreateDeploymentRequest(
                        "payment-service",
                        "v1.0.0",
                        DeploymentEnvironment.DEV
                )
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
                new CreateDeploymentRequest(
                        "payment-service",
                        "v1.0.0",
                        DeploymentEnvironment.DEV
                )
        );

        service.startDeployment(created.id());
        service.markSuccessful(created.id());

        assertThrows(
                InvalidDeploymentStateException.class,
                () -> service.markFailed(created.id())
        );
    }
}
