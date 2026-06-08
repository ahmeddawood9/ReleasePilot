package com.dawood.releasepilot.error;

import java.time.Instant;

// This record represents a clean error response returned by the API.
//
// Example JSON:
// {
//   "timestamp": "...",
//   "status": 404,
//   "error": "Not Found",
//   "message": "Deployment not found with id: 99",
//   "path": "/api/deployments/99"
// }
public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {
}