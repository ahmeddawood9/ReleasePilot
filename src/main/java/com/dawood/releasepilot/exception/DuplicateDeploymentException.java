package com.dawood.releasepilot.exception;

public class DuplicateDeploymentException extends RuntimeException {

    public DuplicateDeploymentException(String serviceName, String version) {
        super("Deployment already exists for service '" + serviceName + "' with version '" + version + "'");
    }
}