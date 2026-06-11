import { AppShell } from "@/components/AppShell";
import { CopyButton } from "@/components/CopyButton";

const requestFields = [
  {
    name: "deploymentId",
    required: "Required",
    description: "The existing ReleasePilot deployment that receives the event.",
  },
  {
    name: "status",
    required: "Required",
    description: "Deployment state reported by the CI/CD system.",
  },
  {
    name: "message",
    required: "Optional",
    description: "Human-readable event text shown in the deployment timeline.",
  },
  {
    name: "provider",
    required: "Optional",
    description: "The external system name, such as GITHUB_ACTIONS or JENKINS.",
  },
  {
    name: "externalDeploymentId",
    required: "Optional",
    description: "The provider-side event or run id used for idempotency.",
  },
  {
    name: "commitSha",
    required: "Optional",
    description: "The source commit connected to this deployment event.",
  },
  {
    name: "branchName",
    required: "Optional",
    description: "The branch that triggered the deployment.",
  },
  {
    name: "triggeredBy",
    required: "Optional",
    description: "The person, bot, or CI user that triggered the event.",
  },
  {
    name: "deploymentUrl",
    required: "Optional",
    description: "A link back to the external CI/CD run or deployment page.",
  },
];

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

const ingestionEndpoint = `${API_BASE_URL}/api/integrations/deployment-events`;

const curlExample = `curl -X POST ${ingestionEndpoint} \\
  -H "Content-Type: application/json" \\
  -H "X-ReleasePilot-Token: local-dev-token" \\
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
  }'`;

export default function IntegrationsPage() {
  return (
    <AppShell>
      <div className="space-y-8">
        <section>
          <p className="text-sm font-semibold uppercase tracking-wide text-slate-500">
            CI/CD ingestion
          </p>
          <h2 className="mt-2 text-2xl font-bold text-slate-900">
            Integrations
          </h2>
          <p className="mt-2 max-w-3xl text-sm leading-6 text-slate-600">
            Integrations let external CI/CD systems send deployment events into
            ReleasePilotLite. Those events are attached to an existing
            deployment timeline, so the app can track what happened after a
            deployment was created. This page is documentation only; it does
            not send ingestion requests from the browser.
          </p>
        </section>

        <section className="grid gap-4 lg:grid-cols-2">
          <div className="rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
            <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">
              Endpoint
            </p>
            <code className="mt-3 block break-all rounded-lg bg-slate-950 p-4 text-sm text-slate-100">
              POST {ingestionEndpoint}
            </code>
          </div>

          <div className="rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
            <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">
              Required header
            </p>
            <code className="mt-3 block break-all rounded-lg bg-slate-950 p-4 text-sm text-slate-100">
              X-ReleasePilot-Token: local-dev-token
            </code>
          </div>
        </section>

        <section className="rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
          <div className="mb-4 flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
            <div>
              <h3 className="text-lg font-semibold text-slate-900">
                Example request
              </h3>
              <p className="mt-1 text-sm text-slate-500">
                Use this shape from GitHub Actions, GitLab, Jenkins, or another
                CI/CD runner after a deployment already exists.
              </p>
            </div>

            <CopyButton value={curlExample} />
          </div>

          <pre className="overflow-x-auto rounded-lg bg-slate-950 p-4 text-sm leading-6 text-slate-100">
            <code>{curlExample}</code>
          </pre>
        </section>

        <section className="rounded-lg border border-slate-200 bg-white shadow-sm">
          <div className="border-b border-slate-200 p-5">
            <h3 className="text-lg font-semibold text-slate-900">
              Request fields
            </h3>
            <p className="mt-1 text-sm text-slate-500">
              The ingestion API stores CI/CD metadata beside the deployment
              event.
            </p>
          </div>

          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-slate-200">
              <thead className="bg-slate-50">
                <tr>
                  <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                    Field
                  </th>
                  <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                    Required
                  </th>
                  <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                    Purpose
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {requestFields.map((field) => (
                  <tr key={field.name}>
                    <td className="px-4 py-4 text-sm font-medium text-slate-900">
                      <code>{field.name}</code>
                    </td>
                    <td className="px-4 py-4 text-sm text-slate-600">
                      {field.required}
                    </td>
                    <td className="px-4 py-4 text-sm text-slate-600">
                      {field.description}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>

        <section className="grid gap-4 lg:grid-cols-2">
          <div className="rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
            <h3 className="text-lg font-semibold text-slate-900">
              Idempotency
            </h3>
            <p className="mt-2 text-sm leading-6 text-slate-600">
              CI/CD tools sometimes retry web requests. ReleasePilotLite uses
              provider, externalDeploymentId, and status to recognize the same
              external event. If the same event is sent again, the API returns
              the existing event instead of creating a duplicate timeline row.
            </p>
          </div>

          <div className="rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
            <h3 className="text-lg font-semibold text-slate-900">
              GitHub Actions support coming next
            </h3>
            <p className="mt-2 text-sm leading-6 text-slate-600">
              The next step is to add a GitHub Actions workflow example that
              calls this ingestion endpoint during deployment jobs. That will
              connect real CI/CD runs to ReleasePilotLite timelines.
            </p>
          </div>
        </section>
      </div>
    </AppShell>
  );
}
