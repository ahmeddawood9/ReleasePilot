# Dashboard Summary API Contract

The dashboard summary endpoint gives the frontend a compact set of deployment counts for the main dashboard cards.

## Endpoint

```text
GET /api/dashboard/summary
```

Local URL:

```text
http://localhost:8080/api/dashboard/summary
```

## Response Shape

```json
{
  "totalDeployments": 0,
  "pendingDeployments": 0,
  "runningDeployments": 0,
  "successfulDeployments": 0,
  "failedDeployments": 0,
  "devDeployments": 0,
  "stagingDeployments": 0,
  "productionDeployments": 0
}
```

## Field Meaning

| Field | Meaning |
| --- | --- |
| `totalDeployments` | Total number of deployments in the database. |
| `pendingDeployments` | Number of deployments currently in `PENDING`. |
| `runningDeployments` | Number of deployments currently in `RUNNING`. |
| `successfulDeployments` | Number of deployments currently in `SUCCESS`. |
| `failedDeployments` | Number of deployments currently in `FAILED`. |
| `devDeployments` | Number of deployments targeting the `DEV` environment. |
| `stagingDeployments` | Number of deployments targeting the `STAGING` environment. |
| `productionDeployments` | Number of deployments targeting the `PRODUCTION` environment. |

## Backend Contract

The backend owns the summary logic in `DashboardService`.

The service queries `DeploymentRepository` for:

- total deployment count
- count by deployment status
- count by deployment environment

The controller returns `200 OK` with `DeploymentDashboardSummary`.

## Frontend Contract

The frontend consumes this endpoint through `getDashboardSummary()` in `frontend/src/lib/api.ts`.

The dashboard page expects every field to be present and numeric. If a count is zero, the backend should return `0`, not `null`.

## Why This Endpoint Exists

The dashboard should not load and count all deployments in the browser. Summary counts are backend-owned because:

- the database can count records efficiently
- the frontend receives a small response
- dashboard card labels stay decoupled from database query details
- future filters or access rules can be applied in one backend service
