package com.dawood.releasepilot.deployment;

import java.util.List;
import java.util.Optional;


// Repository interface = storage contract.
// It says WHAT storage should do, not HOW it does it.

public interface DeploymentRepository { 
    // Save a deployment and return the saved deployment.
    Deployment save(Deployment deployment); 

    //Optional means deployment may exist or may not exist.
    Optional<Deployment> findById(Long id);

    //Return all deployments
    List<Deployment> findAll();

}
