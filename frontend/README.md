# ReleasePilot Frontend

Next.js dashboard for the ReleasePilot API. See the [root README](../README.md) for the full project overview.

## Stack

Next.js 16 (App Router), React 19, TypeScript, Tailwind CSS 4 and TanStack Query 5.

## Routes

| Route | Purpose |
| --- | --- |
| `/dashboard` | Summary counts by status and environment |
| `/deployments` | Paginated list with status and environment filters |
| `/deployments/new` | Create a deployment |
| `/deployments/[id]` | Deployment detail, lifecycle actions and event timeline |
| `/integrations` | Documentation for the CI/CD ingestion API |

## Structure

```text
src/
├── app/          routes (App Router)
├── components/   shared UI: AppShell, StatCard, StatusBadge, ErrorState, CopyButton
├── lib/          typed API client (api.ts) and formatting helpers
├── providers/    TanStack Query provider
└── types/        API request/response types mirroring the backend DTOs
```

## Running

The backend must be running on `http://localhost:8080`. See the root README for how to start it.

```bash
cp .env.example .env.local   # sets NEXT_PUBLIC_API_BASE_URL
npm install
npm run dev                  # http://localhost:3000
```

Checks:

```bash
npm run lint
npm run build
```

`NEXT_PUBLIC_API_BASE_URL` is inlined into the browser bundle at build time. When it changes, rebuild the app or its Docker image.
