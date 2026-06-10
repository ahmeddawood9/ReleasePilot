package com.dawood.releasepilot.exception;

public class InvalidIntegrationTokenException extends RuntimeException {

    public InvalidIntegrationTokenException() {
        super("Invalid or missing integration token");
    }
}
