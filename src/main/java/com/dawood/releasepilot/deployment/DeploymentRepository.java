package com.dawood.releasepilot.deployment;

import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository gives us methods automatically:
//
// save()
// findById()
// findAll()
// deleteById()
// existsById()
// count()
//
// We no longer write implementation manually.
public interface DeploymentRepository extends JpaRepository<Deployment, Long> {
}
