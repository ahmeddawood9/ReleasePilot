package com.dawood.releasepilot.dashboard;

import com.dawood.releasepilot.deployment.DeploymentEnvironment;
import com.dawood.releasepilot.deployment.DeploymentRepository;
import com.dawood.releasepilot.deployment.DeploymentStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DashboardServiceTest {

    @Test
    void shouldReturnDeploymentSummaryCounts() {
        DeploymentRepository deploymentRepository = mock(DeploymentRepository.class);

        when(deploymentRepository.count()).thenReturn(8L);
        when(deploymentRepository.countByStatus(DeploymentStatus.PENDING)).thenReturn(2L);
        when(deploymentRepository.countByStatus(DeploymentStatus.RUNNING)).thenReturn(1L);
        when(deploymentRepository.countByStatus(DeploymentStatus.SUCCESS)).thenReturn(4L);
        when(deploymentRepository.countByStatus(DeploymentStatus.FAILED)).thenReturn(1L);
        when(deploymentRepository.countByEnvironment(DeploymentEnvironment.DEV)).thenReturn(3L);
        when(deploymentRepository.countByEnvironment(DeploymentEnvironment.STAGING)).thenReturn(2L);
        when(deploymentRepository.countByEnvironment(DeploymentEnvironment.PRODUCTION)).thenReturn(3L);

        DashboardService dashboardService = new DashboardService(deploymentRepository);

        DeploymentDashboardSummary summary = dashboardService.getSummary();

        assertEquals(8L, summary.totalDeployments());
        assertEquals(2L, summary.pendingDeployments());
        assertEquals(1L, summary.runningDeployments());
        assertEquals(4L, summary.successfulDeployments());
        assertEquals(1L, summary.failedDeployments());
        assertEquals(3L, summary.devDeployments());
        assertEquals(2L, summary.stagingDeployments());
        assertEquals(3L, summary.productionDeployments());
    }
}
