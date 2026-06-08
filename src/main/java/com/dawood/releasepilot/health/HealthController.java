package com.dawood.releasepilot.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// @RestController tells Spring:
// "This class handles HTTP requests and returns response data directly."
@RestController
public class HealthController {

    // @GetMapping means:
    // When someone sends GET request to /api/health,
    // run this method.
    @GetMapping("/api/health")
    public String health() {
        return "ReleasePilot API is running";
    }
}