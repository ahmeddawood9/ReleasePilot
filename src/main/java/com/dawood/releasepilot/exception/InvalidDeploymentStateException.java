package com.dawood.releasepilot.exception;

public class InvalidDeploymentException extends RuntimeException {

    // Constructor receives an error message.
    // super(message) sends the message to RuntimeException.

    public InvalidDeploymentException(String message) {
        super(message);
    }
}