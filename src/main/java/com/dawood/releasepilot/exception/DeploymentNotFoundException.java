package com.dawood.releasepilot.exception;

public class DeploymentNotFoundException extends RuntimeException {
    // We pass id so the error message tells exactly which deployment was missing.

    public DeploymentNotFoundException(Long id) {
        super("Deployment not found with id: " + id);
    }
}
