package com.dawood.releasepilot.deployment;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// @Repository tells Spring:
// "Create an object of this class and keep it in the application context."
@Repository
public class InMemoryDeploymentRepository implements DeploymentRepository {

    private final Map<Long, Deployment> deployments = new HashMap<>();
    private long nextId = 1;

    @Override
    public Deployment save(Deployment deployment) {
        if (deployment.getId() == null) {
            deployment.assignId(nextId);
            nextId++;
        }

        deployments.put(deployment.getId(), deployment);
        return deployment;
    }

    @Override
    public Optional<Deployment> findById(Long id) {
        return Optional.ofNullable(deployments.get(id));
    }

    @Override
    public List<Deployment> findAll() {
        return new ArrayList<>(deployments.values());
    }
}