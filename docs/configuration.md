# Environment Configuration

ReleasePilot is configured through environment variables so the same code can run locally, in Docker Compose, and later in a production-style deployment.

Do not commit real secrets. Use `.env.example` files for safe local placeholders only.

## Backend Variables

| Variable | Local example | Purpose |
| --- | --- | --- |
| `SPRING_PROFILES_ACTIVE` | `dev` | Selects the Spring profile. Use `dev` locally and `prod` in deployment. |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5433/releasepilot` | JDBC URL for PostgreSQL when running backend outside Docker. |
| `SPRING_DATASOURCE_USERNAME` | `releasepilot` | PostgreSQL username. |
| `SPRING_DATASOURCE_PASSWORD` | `releasepilot` | PostgreSQL password for local demo only. |
| `RELEASEPILOT_INGESTION_TOKEN` | `local-dev-token` | Token required by the CI/CD ingestion endpoint. |
| `FRONTEND_ORIGIN` | `http://localhost:3000` | Allowed browser origin for CORS. Supports comma-separated origins. |

Inside Docker Compose, the backend connects to PostgreSQL by service name:

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/releasepilot
```

Outside Docker Compose, the backend connects through the host-mapped PostgreSQL port:

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/releasepilot
```

## Frontend Variables

| Variable | Local example | Purpose |
| --- | --- | --- |
| `NEXT_PUBLIC_API_BASE_URL` | `http://localhost:8080` | Browser-visible backend API base URL. |

The frontend must use a browser-reachable URL. For local Docker Compose, keep it as:

```text
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080
```

Do not use `http://backend:8080` in the frontend browser bundle. Docker service names work inside the Docker network, but the user's browser cannot resolve them.

## Profile Behavior

`dev` profile:

- uses local/demo defaults
- allows local frontend origins
- validates the schema against Flyway migrations

`prod` profile:

- requires environment-provided database config
- requires environment-provided ingestion token
- requires environment-provided frontend origin
- uses Hibernate validation instead of automatic schema updates

## Local Files

Safe examples:

- `.env.example`
- `frontend/.env.example`

Ignored local files:

- `.env`
- `.env.local`
- `.env*.local`
- `frontend/.env.local`
- `frontend/.env*.local`

These ignored files are where local machine-specific values can live.
