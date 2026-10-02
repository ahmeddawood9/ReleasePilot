# ReleasePilot

A deployment tracking dashboard. It shows which service version went to which environment, its current state, and what the CI/CD pipeline reported along the way.

Built with **Spring Boot**, **PostgreSQL** and **Next.js**. It was deployed on **AWS** (EC2 + RDS) with Docker Compose.

![Dashboard](docs/screenshots/dashboard.png)

| Deployments | Timeline | CI/CD integration |
| --- | --- | --- |
| ![Deployments](docs/screenshots/deployments.png) | ![Deployment detail](docs/screenshots/deployment-detail.png) | ![Integrations](docs/screenshots/integrations.png) |

## Features

- Track deployments by service, version and environment (`DEV`, `STAGING`, `PRODUCTION`)
- Lifecycle `PENDING → RUNNING → SUCCESS / FAILED`, with invalid transitions rejected
- A timeline of events for every deployment
- CI/CD ingestion API secured by a token, with idempotent retries
- Dashboard summary, filtering and pagination
- Flyway migrations, Actuator health checks and Swagger UI

## Tech Stack

| | |
| --- | --- |
| **Backend** | Java 21, Spring Boot 3.5, Spring Data JPA, Bean Validation |
| **Database** | PostgreSQL 17, Flyway |
| **Frontend** | Next.js 16, React 19, TypeScript, Tailwind CSS, TanStack Query |
| **Testing** | JUnit 5, Mockito |
| **Infrastructure** | Docker, Docker Compose, AWS EC2, Amazon RDS |

## Architecture

```text
Browser ──> Next.js :3000 ──> Spring Boot API :8080 ──> PostgreSQL
                                     ^
CI/CD pipeline ──────────────────────┘  POST /api/integrations/deployment-events
```

## Run Locally

```bash
docker compose up --build
```

| Service | URL |
| --- | --- |
| Frontend | http://localhost:3000 |
| API | http://localhost:8080 |
| Swagger | http://localhost:8080/swagger-ui.html |

Run the tests:

```bash
mvn test
```

## API

```text
GET   /api/dashboard/summary
POST  /api/deployments
GET   /api/deployments?status=&environment=&page=&size=
GET   /api/deployments/{id}
GET   /api/deployments/{id}/events
PATCH /api/deployments/{id}/start | /success | /fail
POST  /api/integrations/deployment-events      (X-ReleasePilot-Token header)
```

## Docs

- [CI/CD ingestion](docs/ci-cd-ingestion.md)
- [Dashboard summary](docs/dashboard-summary.md)
- [Configuration](docs/configuration.md)
- [Deploying to AWS (EC2 + RDS)](docs/deployment-aws.md)
- [Frontend](frontend/README.md)

## Scope

This is a finished portfolio project. The following were deliberately left out:

- User authentication and roles (only the ingestion endpoint is protected, by a shared token)
- A CI pipeline
- Integration and end-to-end tests (tests cover the service layer)
- Deleting deployments (they are kept as an audit history)

Known limitation: CI/CD events are added to the timeline but do not change a deployment's status.
