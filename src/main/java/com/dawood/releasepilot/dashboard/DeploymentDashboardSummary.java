package com.dawood.releasepilot.dashboard;

public record DeploymentDashboardSummary(
        long totalDeployments,
        long pendingDeployments,
        long runningDeployments,
        long successfulDeployments,
        long failedDeployments,
        long devDeployments,
        long stagingDeployments,
        long productionDeployments
) {
}
