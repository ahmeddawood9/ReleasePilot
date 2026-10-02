# Deploying to AWS (EC2 + RDS)

The repository includes a separate AWS-oriented Compose file:

```text
docker-compose.aws.yml
```

It runs only:

- Spring Boot backend
- Next.js frontend

PostgreSQL is not included. The backend connects to an external Amazon RDS for PostgreSQL instance.

This setup is intended as an EC2 deployment starting point. It does not create AWS resources and does not replace production infrastructure planning.

## 1. Prepare RDS

Create an RDS PostgreSQL database and note:

- RDS endpoint
- database port, normally `5432`
- database name
- database username
- database password

The JDBC URL must use this format:

```text
jdbc:postgresql://RDS_ENDPOINT:5432/releasepilot
```

Configure security groups so:

- RDS allows PostgreSQL port `5432` only from the EC2 instance security group
- RDS is not publicly open to `0.0.0.0/0`
- EC2 allows SSH only from a trusted administrator IP
- EC2 allows frontend/backend ports only as required for the demo

For a public demo using the current ports:

- frontend: TCP `3000`
- backend: TCP `8080`

A later production setup should normally place these services behind HTTPS and a reverse proxy or load balancer instead of exposing application ports directly.

## 2. Prepare EC2

Install:

- Git
- Docker
- Docker Compose

Clone the repository and enter the project directory:

```bash
git clone git@github.com:ahmeddawood9/ReleasePilot.git
cd ReleasePilot
```

## 3. Configure Environment Variables

Create the EC2 environment file from the safe template:

```bash
cp .env.aws.example .env.aws
```

Edit `.env.aws` and replace every placeholder:

```text
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
RELEASEPILOT_INGESTION_TOKEN
FRONTEND_ORIGIN
NEXT_PUBLIC_API_BASE_URL
```

Use the public HTTPS URLs when a domain and TLS are configured. For an initial EC2 demo, the origins can use the EC2 public IP and ports `3000` and `8080`.

Do not commit `.env.aws`. It is ignored by Git.

`NEXT_PUBLIC_API_BASE_URL` is embedded into the browser bundle during the frontend Docker build. Rebuild the frontend image whenever this value changes.

## 4. Start the EC2 Stack

Build and start the backend and frontend:

```bash
docker compose \
  --env-file .env.aws \
  -f docker-compose.aws.yml \
  up --build -d
```

Check status and logs:

```bash
docker compose --env-file .env.aws -f docker-compose.aws.yml ps
docker compose --env-file .env.aws -f docker-compose.aws.yml logs -f
```

The backend starts with:

```text
SPRING_PROFILES_ACTIVE=prod
```

Flyway applies the database migration to an empty RDS database, and Hibernate validates the resulting schema.

## 5. Verify the Deployment

Replace `EC2_PUBLIC_IP` with the instance public IP or domain:

```text
Frontend: http://EC2_PUBLIC_IP:3000
Backend health: http://EC2_PUBLIC_IP:8080/actuator/health
Swagger: http://EC2_PUBLIC_IP:8080/swagger-ui.html
```

Stop the application containers:

```bash
docker compose --env-file .env.aws -f docker-compose.aws.yml down
```

This command does not delete RDS data because the database is external to Docker Compose.
