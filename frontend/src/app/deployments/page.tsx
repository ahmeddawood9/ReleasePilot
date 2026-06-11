"use client";

import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import Link from "next/link";
import { AppShell } from "@/components/AppShell";
import { ErrorState } from "@/components/ErrorState";
import { StatusBadge } from "@/components/StatusBadge";
import { getDeployments } from "@/lib/api";
import { formatDateTime } from "@/lib/format";
import type { DeploymentEnvironment, DeploymentStatus } from "@/types/api";

const statuses: Array<DeploymentStatus | "ALL"> = [
  "ALL",
  "PENDING",
  "RUNNING",
  "SUCCESS",
  "FAILED",
];

const environments: Array<DeploymentEnvironment | "ALL"> = [
  "ALL",
  "DEV",
  "STAGING",
  "PRODUCTION",
];

export default function DeploymentsPage() {
  const [page, setPage] = useState(0);
  const [status, setStatus] = useState<DeploymentStatus | "ALL">("ALL");
  const [environment, setEnvironment] =
    useState<DeploymentEnvironment | "ALL">("ALL");

  const { data, isLoading, isError, error, refetch } = useQuery({
    queryKey: ["deployments", page, status, environment],
    queryFn: () =>
      getDeployments({
        page,
        size: 10,
        status: status === "ALL" ? undefined : status,
        environment: environment === "ALL" ? undefined : environment,
      }),
  });

  return (
    <AppShell>
      <div className="mb-8 flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
        <div>
          <h2 className="text-2xl font-bold text-slate-900">Deployments</h2>
          <p className="mt-1 text-sm text-slate-500">
            View tracked deployments across environments and statuses.
          </p>
        </div>

        <div className="flex flex-col gap-3 sm:flex-row sm:items-end">
          <Link
            href="/deployments/new"
            className="rounded-lg bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-700"
          >
            Create deployment
          </Link>

          <label className="text-sm text-slate-600">
            Status
            <select
              value={status}
              onChange={(event) => {
                setStatus(event.target.value as DeploymentStatus | "ALL");
                setPage(0);
              }}
              className="mt-1 block rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm text-slate-900"
            >
              {statuses.map((item) => (
                <option key={item} value={item}>
                  {item}
                </option>
              ))}
            </select>
          </label>

          <label className="text-sm text-slate-600">
            Environment
            <select
              value={environment}
              onChange={(event) => {
                setEnvironment(
                  event.target.value as DeploymentEnvironment | "ALL"
                );
                setPage(0);
              }}
              className="mt-1 block rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm text-slate-900"
            >
              {environments.map((item) => (
                <option key={item} value={item}>
                  {item}
                </option>
              ))}
            </select>
          </label>
        </div>
      </div>

      {isLoading ? (
        <p className="text-slate-600">Loading deployments...</p>
      ) : null}

      {isError ? (
        <ErrorState
          title="Failed to load deployments"
          message={error instanceof Error ? error.message : String(error)}
          onRetry={() => {
            void refetch();
          }}
        />
      ) : null}

      {data ? (
        <div className="overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm">
          <table className="min-w-full divide-y divide-slate-200">
            <thead className="bg-slate-50">
              <tr>
                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                  Service
                </th>
                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                  Version
                </th>
                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                  Environment
                </th>
                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                  Status
                </th>
                <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                  Created
                </th>
                <th className="px-4 py-3 text-right text-xs font-semibold uppercase tracking-wide text-slate-500">
                  Action
                </th>
              </tr>
            </thead>

            <tbody className="divide-y divide-slate-100">
              {data.content.length === 0 ? (
                <tr>
                  <td
                    colSpan={6}
                    className="px-4 py-8 text-center text-sm text-slate-500"
                  >
                    No deployments found.
                  </td>
                </tr>
              ) : (
                data.content.map((deployment) => (
                  <tr key={deployment.id} className="hover:bg-slate-50">
                    <td className="px-4 py-4 text-sm font-medium text-slate-900">
                      {deployment.serviceName}
                    </td>
                    <td className="px-4 py-4 text-sm text-slate-600">
                      {deployment.version}
                    </td>
                    <td className="px-4 py-4 text-sm text-slate-600">
                      {deployment.environment}
                    </td>
                    <td className="px-4 py-4 text-sm">
                      <StatusBadge status={deployment.status} size="xs" />
                    </td>
                    <td className="px-4 py-4 text-sm text-slate-600">
                      {formatDateTime(deployment.createdAt)}
                    </td>
                    <td className="px-4 py-4 text-right text-sm">
                      <Link
                        href={`/deployments/${deployment.id}`}
                        className="font-medium text-slate-900 hover:underline"
                      >
                        View
                      </Link>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>

          <div className="flex items-center justify-between border-t border-slate-200 px-4 py-3">
            <p className="text-sm text-slate-500">
              Page {data.number + 1} of {Math.max(data.totalPages, 1)} ·{" "}
              {data.totalElements} deployments
            </p>

            <div className="flex gap-2">
              <button
                onClick={() => setPage((current) => Math.max(current - 1, 0))}
                disabled={page === 0}
                className="rounded-lg border border-slate-300 px-3 py-2 text-sm font-medium text-slate-700 disabled:cursor-not-allowed disabled:opacity-50"
              >
                Previous
              </button>

              <button
                onClick={() =>
                  setPage((current) =>
                    data.totalPages > 0
                      ? Math.min(current + 1, data.totalPages - 1)
                      : current
                  )
                }
                disabled={data.totalPages === 0 || page >= data.totalPages - 1}
                className="rounded-lg border border-slate-300 px-3 py-2 text-sm font-medium text-slate-700 disabled:cursor-not-allowed disabled:opacity-50"
              >
                Next
              </button>
            </div>
          </div>
        </div>
      ) : null}
    </AppShell>
  );
}
