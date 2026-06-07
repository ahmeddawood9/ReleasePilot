# ReleasePilot Lite

ReleasePilot Lite is a plain Java backend-style project for learning production backend structure before moving to Spring Boot.

The project models a simple deployment management system where deployments can be created, started, marked successful, or marked failed.

## Purpose

This project is not a console app for the sake of console apps.

It is designed to practice backend architecture in plain Java:

- domain models
- DTOs
- service layer
- repository layer
- custom exceptions
- Optional
- streams
- timestamps
- Maven
- JUnit tests

Later, this structure will be migrated to a Spring Boot REST API.

## Current Features

- Create a deployment
- List deployments
- Find deployment by ID
- Start deployment
- Mark deployment as successful
- Mark deployment as failed
- Prevent invalid status transitions
- Track deployment timestamps
- Test service-layer behavior with JUnit

## Deployment Status Flow

```text
PENDING
   |
   v
RUNNING
   |       |
   v       v
SUCCESS  FAILED
```

Rules:

- PENDING can become RUNNING
- RUNNING can become SUCCESS
- RUNNING can become FAILED
- SUCCESS is final
- FAILED is final

## Project Structure

```text
src/main/java/com/dawood/releasepilot
├── Main.java
├── deployment
│   ├── CreateDeploymentRequest.java
│   ├── Deployment.java
│   ├── DeploymentRepository.java
│   ├── DeploymentResponse.java
│   ├── DeploymentService.java
│   ├── DeploymentStatus.java
│   └── InMemoryDeploymentRepository.java
└── exception
    ├── DeploymentNotFoundException.java
    └── InvalidDeploymentStateException.java
```

Test structure:

```text
src/test/java/com/dawood/releasepilot/deployment
└── DeploymentServiceTest.java
```

## Architecture Flow

```text
Main.java
   |
   v
DeploymentService
   |
   v
DeploymentRepository
   |
   v
InMemoryDeploymentRepository
   |
   v
Map<Long, Deployment>
```

Later Spring Boot version:

```text
HTTP Request
   |
   v
DeploymentController
   |
   v
DeploymentService
   |
   v
DeploymentRepository
   |
   v
PostgreSQL
   |
   v
DeploymentResponse JSON
```

## Tech Used

- Java 17/21
- Maven
- JUnit 5

## How to Run

Compile the project:

```bash
mvn clean compile
```

Run the app:

```bash
java -cp target/classes com.dawood.releasepilot.Main
```

Run tests:

```bash
mvn test
```

## What I Learned

This project helped me understand how backend code is structured before using Spring Boot.

Main concepts practiced:

- separating domain, service, repository, and DTOs
- using custom exceptions for business errors
- using Optional for not-found cases
- using streams for DTO mapping
- using Instant for backend timestamps
- writing JUnit tests for service behavior
- using Maven for builds and dependencies

## Next Step

The next version will migrate this plain Java project to Spring Boot with:

- REST controllers
- Spring services
- Spring Data JPA
- PostgreSQL
- validation
- global exception handling
- Docker
