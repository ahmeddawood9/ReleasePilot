package com.dawood.releasepilot.deployment;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


// This is a fake database for now.
// It stores deployments inside memory using HashMap.
//
// Later, Spring Boot + PostgreSQL will replace this.
public class InMemoryDeploymentRepository implements DeploymentRepository {

    // Map<Long, Deployment>
    // key   = deployment id
    // value = deployment object
    private final Map<Long, Deployment> deployments = new HashMap<>();

    // Simple ID generator.
    // First deployment gets ID 1, then 2, then 3...
    private long nextId = 1;

    @Override
    public Deployment save(Deployment deployment) {

        // If deployment has no ID, it means it is new.
        if (deployment.getId() == null) {
            deployment.assignId(nextId);
            nextId++;
        }

        // Put deployment in map.
        // If ID already exists, it updates the existing deployment.
        deployments.put(deployment.getId(), deployment);

        return deployment;
    }

    @Override
    public Optional<Deployment> findById(Long id) {

        // deployments.get(id) returns:
        // - Deployment object if found
        // - null if not found
        //
        // Optional.ofNullable converts null into Optional.empty().
        return Optional.ofNullable(deployments.get(id));
    }

    @Override
    public List<Deployment> findAll() {

        // deployments.values() gives all Deployment objects.
        // new ArrayList<>(...) makes a separate list copy.
        return new ArrayList<>(deployments.values());
    }
}