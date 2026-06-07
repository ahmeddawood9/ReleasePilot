package com.dawood.releasepilot.exception;

public class InvalidDeploymentStateException extends RuntimeException {

    // Constructor receives an error message.
    // super(message) sends the message to RuntimeException.

    public InvalidDeploymentStateException(String message) {
        super(message);
    }
}
