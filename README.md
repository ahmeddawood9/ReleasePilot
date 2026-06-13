# ReleasePilotLite

ReleasePilotLite is a Spring Boot and Next.js deployment tracking dashboard for learning production-style backend and frontend architecture.

It models how teams track releases across environments, collect deployment timeline events, and prepare a service for containerized deployment.

## Problem Statement

Small teams often deploy from CI/CD tools but lack a simple place to see what was deployed, where it was deployed, who triggered it, and how the deployment state changed over time.

ReleasePilotLite solves that learning problem with a focused deployment tracking system:

- create and track deployments
- move deployments through a controlled lifecycle
- record timeline events
- ingest external CI/CD events
- expose dashboard-ready APIs
- run the full stack locally with Docker Compose

## Tech Stack

- Backend: Java 21, Spring Boot, Spring Web, Spring Data JPA, Validation
- Database: PostgreSQL
- Migrations: Flyway
- API docs: Springdoc OpenAPI / Swagger UI
- Health checks: Spring Boot Actuator
- Frontend: Next.js App Router, TypeScript, Tailwind CSS, TanStack Query
- Local runtime: Docker Compose
- Testing: JUnit 5, Maven

## Architecture

```text
Browser
  |
  v
Next.js Frontend :3000
  |
  v
Spring Boot API :8080
  |
  v
Service Layer
  |
  v
Spring Data JPA Repositories
  |
  v
PostgreSQL :5433 on host, :5432 inside Docker

CI/CD Tool
  |
  v
POST /api/integrations/deployment-events
  |
  v
Deployment Timeline
```

## Core Features

- Dashboard summary for deployments by status and environment
- Deployment tracking with service name, version, environment, and timestamps
- Deployment lifecycle: `PENDING -> RUNNING -> SUCCESS` or `PENDING -> RUNNING -> FAILED`
- Final states: `SUCCESS` and `FAILED`
- Deployment detail page with timeline events
- CI/CD ingestion API for external deployment events
- Idempotency for external events using provider, external deployment ID, and status
- Duplicate deployment protection using service name, version, and environment
- Filtering and pagination for deployment lists
- Docker Compose full-stack setup
- Swagger API documentation
- Actuator health endpoint
- Flyway database migrations

## Backend API Overview

Dashboard:

```text
GET /api/dashboard/summary
```

Deployments:

```text
POST  /api/deployments
GET   /api/deployments?page=0&size=10
GET   /api/deployments?status=RUNNING
GET   /api/deployments?environment=PRODUCTION
GET   /api/deployments/{id}
GET   /api/deployments/{id}/events
PATCH /api/deployments/{id}/start
PATCH /api/deployments/{id}/success
PATCH /api/deployments/{id}/fail
```

Integrations:

```text
POST /api/integrations/deployment-events
Header: X-ReleasePilot-Token: local-dev-token
```

Health:

```text
GET /actuator/health
```

## CI/CD Ingestion

ReleasePilotLite can receive external deployment events from CI/CD systems such as GitHub Actions, GitLab, or Jenkins.

The ingestion API attaches an event to an existing deployment timeline. It does not create deployments automatically.

Idempotency is handled by:

```text
provider + externalDeploymentId + status
```

If the same CI/CD system retries the same event, ReleasePilotLite returns the existing event instead of creating a duplicate.

## Local Development Setup

Prerequisites:

- Java 21
- Maven
- Node.js 22 or compatible LTS
- Docker and Docker Compose

Run backend tests:

```bash
mvn test
```

Run only PostgreSQL for manual backend/frontend development:

```bash
docker compose up -d postgres
mvn spring-boot:run
cd frontend
npm run dev
```

Manual local URLs:

- Frontend: http://localhost:3000
- Backend: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html
- Health: http://localhost:8080/actuator/health

## Full Docker Compose Setup

Run the full local demo stack:

```bash
docker compose up --build
```

This starts:

- PostgreSQL on host port `5433`
- Spring Boot backend on host port `8080`
- Next.js frontend on host port `3000`

Reset the local Docker database:

```bash
docker compose down -v
```

Use this when you want a fresh local PostgreSQL volume, especially after introducing Flyway migrations to a database that was previously created by Hibernate.

## Environment Variables

The root `.env.example` contains safe local/demo placeholders:

```text
POSTGRES_DB
POSTGRES_USER
POSTGRES_PASSWORD
SPRING_PROFILES_ACTIVE
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
RELEASEPILOT_INGESTION_TOKEN
FRONTEND_ORIGIN
```

The frontend `frontend/.env.example` contains:

```text
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080
```

Do not commit `.env`, `.env.local`, or real production secrets.

## Profiles

The backend uses Spring profiles:

- `dev`: local/demo defaults for Docker Compose and manual development
- `prod`: requires database URL, database credentials, ingestion token, and frontend origin from environment variables

Production uses:

```text
spring.jpa.hibernate.ddl-auto=validate
```

Flyway owns schema changes. Hibernate validates that entity mappings match the migrated database schema.

## Screenshots

Screenshots will be added after final UI capture.

- Dashboard screenshot
- Deployments list screenshot
- Deployment detail/timeline screenshot
- Integrations page screenshot

## Git Hygiene

Good files to commit:

- `README.md`
- `pom.xml`
- `src/`
- `Dockerfile`
- `docker-compose.yml`
- `.dockerignore`
- `.gitignore`
- `.env.example`
- `frontend/package.json`
- `frontend/package-lock.json`
- `frontend/src/`
- `frontend/Dockerfile`
- `frontend/.dockerignore`
- `frontend/.env.example`
- `src/main/resources/db/migration/*.sql`

Do not commit:

- `target/`
- `frontend/node_modules/`
- `frontend/.next/`
- `.env`
- `.env.local`
- `frontend/.env.local`
- logs
- IDE folders
- real secrets
- local machine-specific files
