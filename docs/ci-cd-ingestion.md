# CI/CD Deployment Event Ingestion

ReleasePilotLite can receive deployment events from external CI/CD systems such as GitHub Actions, GitLab, Jenkins, or any custom release job.

The ingestion endpoint appends an event to an existing deployment timeline. It does not create deployments automatically. A deployment must already exist in ReleasePilotLite before an external event can be attached to it.

## Endpoint

```text
POST /api/integrations/deployment-events
```

Local URL:

```text
http://localhost:8080/api/integrations/deployment-events
```

Required header:

```text
X-ReleasePilot-Token: local-dev-token
```

The local token is only a development/demo value. Production deployments should provide `RELEASEPILOT_INGESTION_TOKEN` through the environment or a secrets manager.

## Request Body

```json
{
  "deploymentId": 1,
  "status": "RUNNING",
  "message": "GitHub Actions deployment started",
  "provider": "GITHUB_ACTIONS",
  "externalDeploymentId": "gha-123",
  "commitSha": "abc123",
  "branchName": "main",
  "triggeredBy": "dawood",
  "deploymentUrl": "https://github.com/example/actions/runs/123"
}
```

## Field Meaning

| Field | Meaning |
| --- | --- |
| `deploymentId` | Existing ReleasePilotLite deployment ID that receives the event. |
| `status` | Deployment status represented by the event: `PENDING`, `RUNNING`, `SUCCESS`, or `FAILED`. |
| `message` | Human-readable event message shown in the deployment timeline. |
| `provider` | External system name, for example `GITHUB_ACTIONS`, `GITLAB`, or `JENKINS`. |
| `externalDeploymentId` | Unique deployment/run ID from the external provider. |
| `commitSha` | Commit hash associated with the release. |
| `branchName` | Branch that triggered the deployment. |
| `triggeredBy` | User, actor, or automation account that started the deployment. |
| `deploymentUrl` | Link back to the CI/CD run or deployment page. |

## Idempotency

CI/CD systems often retry failed HTTP calls. Without idempotency, the same external deployment event could appear multiple times in the timeline.

ReleasePilotLite treats this combination as the idempotency key:

```text
provider + externalDeploymentId + status
```

If the same provider sends the same external deployment ID with the same status again, ReleasePilotLite returns the existing timeline event instead of creating a duplicate.

## Curl Example

```bash
curl -X POST http://localhost:8080/api/integrations/deployment-events \
  -H "Content-Type: application/json" \
  -H "X-ReleasePilot-Token: local-dev-token" \
  -d '{
    "deploymentId": 1,
    "status": "RUNNING",
    "message": "GitHub Actions deployment started",
    "provider": "GITHUB_ACTIONS",
    "externalDeploymentId": "gha-123",
    "commitSha": "abc123",
    "branchName": "main",
    "triggeredBy": "dawood",
    "deploymentUrl": "https://github.com/example/actions/runs/123"
  }'
```

## Expected Responses

- `202 Accepted`: event was recorded, or an existing idempotent event was returned.
- `401 Unauthorized`: token header is missing or invalid.
- `404 Not Found`: deployment ID does not exist.
- `400 Bad Request`: request validation failed.
