package com.dawood.releasepilot.exception;

public class DuplicateDeploymentException extends RuntimeException {

    public DuplicateDeploymentException(String serviceName, String version, String environment) {
        super("Deployment already exists for service '" + serviceName
                + "' with version '" + version
                + "' in environment '" + environment + "'");
    }
}
