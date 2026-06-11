"use client";

import { useMemo } from "react";
import { useParams } from "next/navigation";
import Link from "next/link";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { AppShell } from "@/components/AppShell";
import { ErrorState } from "@/components/ErrorState";
import { StatusBadge } from "@/components/StatusBadge";
import {
  ApiError,
  getDeployment,
  getDeploymentEvents,
  markDeploymentFailed,
  markDeploymentSuccess,
  startDeployment,
} from "@/lib/api";
import { formatDateTime } from "@/lib/format";

function MetadataItem({
  label,
  value,
}: {
  label: string;
  value: string | number | null;
}) {
  return (
    <div className="rounded-lg border border-slate-200 bg-white p-4">
      <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">
        {label}
      </p>
      <p className="mt-2 text-sm font-medium text-slate-900">
        {value || "-"}
      </p>
    </div>
  );
}

function MetadataMini({
  label,
  value,
}: {
  label: string;
  value: string | null;
}) {
  return (
    <div>
      <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">
        {label}
      </p>
      <p className="mt-1 break-all text-sm font-medium text-slate-700">
        {value || "-"}
      </p>
    </div>
  );
}

function StateMessage({
  title,
  message,
  onRetry,
}: {
  title: string;
  message: string;
  onRetry?: () => void;
}) {
  return (
    <AppShell>
      <div className="rounded-lg border border-red-200 bg-red-50 p-5 text-red-700">
        <h2 className="text-lg font-semibold">{title}</h2>
        <p className="mt-2 text-sm">{message}</p>
        <div className="mt-4 flex flex-wrap gap-3">
          {onRetry ? (
            <button
              type="button"
              onClick={onRetry}
              className="rounded-lg border border-red-200 bg-white px-3 py-2 text-sm font-medium text-red-700 hover:bg-red-50"
            >
              Retry
            </button>
          ) : null}

          <Link
            href="/deployments"
            className="inline-flex rounded-lg border border-red-200 bg-white px-3 py-2 text-sm font-medium text-red-700 hover:bg-red-50"
          >
            Back to deployments
          </Link>
        </div>
      </div>
    </AppShell>
  );
}

export default function DeploymentDetailPage() {
  const params = useParams<{ id: string }>();
  const queryClient = useQueryClient();

  const deploymentId = useMemo(() => Number(params.id), [params.id]);

  const isValidId = Number.isFinite(deploymentId) && deploymentId > 0;

  const deploymentQuery = useQuery({
    queryKey: ["deployment", deploymentId],
    queryFn: () => getDeployment(deploymentId),
    enabled: isValidId,
  });

  const eventsQuery = useQuery({
    queryKey: ["deployment-events", deploymentId],
    queryFn: () => getDeploymentEvents(deploymentId),
    enabled: isValidId,
  });

  async function refreshDeploymentData() {
    await Promise.all([
      queryClient.invalidateQueries({ queryKey: ["deployment", deploymentId] }),
      queryClient.invalidateQueries({
        queryKey: ["deployment-events", deploymentId],
      }),
      queryClient.invalidateQueries({ queryKey: ["deployments"] }),
      queryClient.invalidateQueries({ queryKey: ["dashboard-summary"] }),
    ]);
  }

  const startMutation = useMutation({
    mutationFn: () => startDeployment(deploymentId),
    onSuccess: refreshDeploymentData,
  });

  const successMutation = useMutation({
    mutationFn: () => markDeploymentSuccess(deploymentId),
    onSuccess: refreshDeploymentData,
  });

  const failedMutation = useMutation({
    mutationFn: () => markDeploymentFailed(deploymentId),
    onSuccess: refreshDeploymentData,
  });

  const actionError =
    startMutation.error ?? successMutation.error ?? failedMutation.error;

  if (!isValidId) {
    return (
      <StateMessage
        title="Invalid deployment"
        message="The deployment id in the URL is not valid."
      />
    );
  }

  if (deploymentQuery.isLoading) {
    return (
      <AppShell>
        <p className="text-slate-600">Loading deployment...</p>
      </AppShell>
    );
  }

  if (deploymentQuery.isError) {
    if (
      deploymentQuery.error instanceof ApiError &&
      deploymentQuery.error.status === 404
    ) {
      return (
        <StateMessage
          title="Deployment not found"
          message="No deployment exists for this id."
        />
      );
    }

    return (
      <StateMessage
        title="Failed to load deployment"
        message={
          deploymentQuery.error instanceof Error
            ? deploymentQuery.error.message
            : String(deploymentQuery.error)
        }
        onRetry={() => {
          void deploymentQuery.refetch();
        }}
      />
    );
  }

  const deployment = deploymentQuery.data;

  if (!deployment) {
    return null;
  }

  return (
    <AppShell>
      <div className="mb-8">
        <Link
          href="/deployments"
          className="text-sm font-medium text-slate-600 hover:text-slate-900"
        >
          Back to deployments
        </Link>

        <div className="mt-4 flex flex-col gap-3 md:flex-row md:items-start md:justify-between">
          <div>
            <h2 className="text-2xl font-bold text-slate-900">
              {deployment.serviceName}
            </h2>
            <p className="mt-1 text-sm text-slate-500">
              Version {deployment.version} - {deployment.environment}
            </p>
          </div>

          <StatusBadge status={deployment.status} />
        </div>

        <div className="mt-5 flex flex-wrap gap-3">
          {deployment.status === "PENDING" ? (
            <button
              onClick={() => startMutation.mutate()}
              disabled={startMutation.isPending}
              className="rounded-lg bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-700 disabled:cursor-not-allowed disabled:opacity-60"
            >
              {startMutation.isPending ? "Starting..." : "Start"}
            </button>
          ) : null}

          {deployment.status === "RUNNING" ? (
            <>
              <button
                onClick={() => successMutation.mutate()}
                disabled={successMutation.isPending || failedMutation.isPending}
                className="rounded-lg bg-green-700 px-4 py-2 text-sm font-medium text-white hover:bg-green-800 disabled:cursor-not-allowed disabled:opacity-60"
              >
                {successMutation.isPending ? "Saving..." : "Mark Success"}
              </button>

              <button
                onClick={() => failedMutation.mutate()}
                disabled={successMutation.isPending || failedMutation.isPending}
                className="rounded-lg bg-red-700 px-4 py-2 text-sm font-medium text-white hover:bg-red-800 disabled:cursor-not-allowed disabled:opacity-60"
              >
                {failedMutation.isPending ? "Saving..." : "Mark Failed"}
              </button>
            </>
          ) : null}

          {deployment.status === "SUCCESS" || deployment.status === "FAILED" ? (
            <p className="rounded-lg border border-slate-200 bg-white px-4 py-2 text-sm text-slate-500">
              Final state reached. No further lifecycle actions are available.
            </p>
          ) : null}
        </div>

        {actionError ? (
          <div className="mt-4 rounded-lg border border-red-200 bg-red-50 p-4 text-sm text-red-700">
            {actionError instanceof Error
              ? actionError.message
              : String(actionError)}
          </div>
        ) : null}
      </div>

      <section className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <MetadataItem label="Deployment ID" value={deployment.id} />
        <MetadataItem label="Environment" value={deployment.environment} />
        <MetadataItem
          label="Created at"
          value={formatDateTime(deployment.createdAt)}
        />
        <MetadataItem
          label="Started at"
          value={formatDateTime(deployment.startedAt)}
        />
        <MetadataItem
          label="Completed at"
          value={formatDateTime(deployment.completedAt)}
        />
      </section>

      <section className="mt-8">
        <div className="mb-4">
          <h3 className="text-lg font-semibold text-slate-900">
            Deployment timeline
          </h3>
          <p className="mt-1 text-sm text-slate-500">
            History of events recorded for this deployment.
          </p>
        </div>

        {eventsQuery.isLoading ? (
          <p className="text-slate-600">Loading timeline...</p>
        ) : null}

        {eventsQuery.isError ? (
          <ErrorState
            title="Failed to load timeline"
            message={
              eventsQuery.error instanceof Error
                ? eventsQuery.error.message
                : String(eventsQuery.error)
            }
            onRetry={() => {
              void eventsQuery.refetch();
            }}
          />
        ) : null}

        {eventsQuery.data ? (
          <div className="overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm">
            {eventsQuery.data.length === 0 ? (
              <div className="p-8 text-center text-sm text-slate-500">
                No timeline events found.
              </div>
            ) : (
              <ol className="divide-y divide-slate-100">
                {[...eventsQuery.data]
                  .sort(
                    (first, second) =>
                      new Date(first.occurredAt).getTime() -
                      new Date(second.occurredAt).getTime()
                  )
                  .map((event) => (
                    <li key={event.id} className="p-5">
                      <div className="flex flex-col gap-3 md:flex-row md:items-start md:justify-between">
                        <div>
                          <StatusBadge status={event.status} size="xs" />

                          <p className="mt-3 text-sm font-medium text-slate-900">
                            {event.message || "Deployment event recorded"}
                          </p>

                          <p className="mt-1 text-sm text-slate-500">
                            Occurred at {formatDateTime(event.occurredAt)}
                          </p>
                        </div>

                        <p className="text-xs text-slate-400">
                          Created {formatDateTime(event.createdAt)}
                        </p>
                      </div>

                      {(event.provider ||
                        event.externalDeploymentId ||
                        event.commitSha ||
                        event.branchName ||
                        event.triggeredBy ||
                        event.deploymentUrl) && (
                        <div className="mt-4 grid gap-3 rounded-lg bg-slate-50 p-4 text-sm sm:grid-cols-2 lg:grid-cols-3">
                          <MetadataMini
                            label="Provider"
                            value={event.provider}
                          />
                          <MetadataMini
                            label="External ID"
                            value={event.externalDeploymentId}
                          />
                          <MetadataMini
                            label="Commit"
                            value={event.commitSha}
                          />
                          <MetadataMini
                            label="Branch"
                            value={event.branchName}
                          />
                          <MetadataMini
                            label="Triggered by"
                            value={event.triggeredBy}
                          />

                          <div>
                            <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">
                              Deployment URL
                            </p>

                            {event.deploymentUrl ? (
                              <a
                                href={event.deploymentUrl}
                                target="_blank"
                                rel="noreferrer"
                                className="mt-1 block break-all text-sm font-medium text-blue-700 hover:underline"
                              >
                                Open external deployment
                              </a>
                            ) : (
                              <p className="mt-1 text-sm text-slate-700">-</p>
                            )}
                          </div>
                        </div>
                      )}
                    </li>
                  ))}
              </ol>
            )}
          </div>
        ) : null}
      </section>
    </AppShell>
  );
}
