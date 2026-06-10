package com.dawood.releasepilot.dashboard;

import com.dawood.releasepilot.deployment.DeploymentEnvironment;
import com.dawood.releasepilot.deployment.DeploymentRepository;
import com.dawood.releasepilot.deployment.DeploymentStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {

    private final DeploymentRepository deploymentRepository;

    public DashboardService(DeploymentRepository deploymentRepository) {
        this.deploymentRepository = deploymentRepository;
    }

    @Transactional(readOnly = true)
    public DeploymentDashboardSummary getSummary() {
        return new DeploymentDashboardSummary(
                deploymentRepository.count(),
                deploymentRepository.countByStatus(DeploymentStatus.PENDING),
                deploymentRepository.countByStatus(DeploymentStatus.RUNNING),
                deploymentRepository.countByStatus(DeploymentStatus.SUCCESS),
                deploymentRepository.countByStatus(DeploymentStatus.FAILED),
                deploymentRepository.countByEnvironment(DeploymentEnvironment.DEV),
                deploymentRepository.countByEnvironment(DeploymentEnvironment.STAGING),
                deploymentRepository.countByEnvironment(DeploymentEnvironment.PRODUCTION)
        );
    }
}
