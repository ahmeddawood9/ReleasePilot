"use client";

import { AppShell } from "@/components/AppShell";
import { ErrorState } from "@/components/ErrorState";
import { StatCard } from "@/components/StatCard";
import { getDashboardSummary } from "@/lib/api";
import { useQuery } from "@tanstack/react-query";

export default function DashboardPage() {
  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ["dashboard-summary"],
    queryFn: getDashboardSummary,
  });

  return (
    <AppShell>
      <div className="space-y-6">
        <div>
          <h2 className="text-2xl font-bold text-slate-900">Dashboard</h2>
          <p className="mt-1 text-sm text-slate-500">
            Deployment activity summary
          </p>
        </div>

        {isLoading ? (
          <p className="text-sm text-slate-500">Loading dashboard...</p>
        ) : null}

        {error ? (
          <ErrorState
            title="Failed to load dashboard summary"
            message={error instanceof Error ? error.message : String(error)}
            onRetry={() => {
              void refetch();
            }}
          />
        ) : null}

        {data ? (
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
            <StatCard
              label="Total deployments"
              value={data.totalDeployments}
              description="All tracked deployments"
            />
            <StatCard
              label="Pending"
              value={data.pendingDeployments}
              description="Waiting to start"
            />
            <StatCard
              label="Running"
              value={data.runningDeployments}
              description="Currently active"
            />
            <StatCard
              label="Successful"
              value={data.successfulDeployments}
              description="Completed successfully"
            />
            <StatCard
              label="Failed"
              value={data.failedDeployments}
              description="Completed with failure"
            />
            <StatCard
              label="DEV"
              value={data.devDeployments}
              description="Development deployments"
            />
            <StatCard
              label="STAGING"
              value={data.stagingDeployments}
              description="Staging deployments"
            />
            <StatCard
              label="PRODUCTION"
              value={data.productionDeployments}
              description="Production deployments"
            />
          </div>
        ) : null}
      </div>
    </AppShell>
  );
}
