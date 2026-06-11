"use client";

import { FormEvent, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { AppShell } from "@/components/AppShell";
import { createDeployment } from "@/lib/api";
import type { DeploymentEnvironment } from "@/types/api";

const environments: DeploymentEnvironment[] = ["DEV", "STAGING", "PRODUCTION"];

export default function NewDeploymentPage() {
  const router = useRouter();
  const queryClient = useQueryClient();
  const [serviceName, setServiceName] = useState("");
  const [version, setVersion] = useState("");
  const [environment, setEnvironment] =
    useState<DeploymentEnvironment>("DEV");
  const [formError, setFormError] = useState<string | null>(null);

  const mutation = useMutation({
    mutationFn: createDeployment,
    onSuccess: async (deployment) => {
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: ["deployments"] }),
        queryClient.invalidateQueries({ queryKey: ["dashboard-summary"] }),
      ]);

      router.push(`/deployments/${deployment.id}`);
    },
    onError: (error) => {
      setFormError(error instanceof Error ? error.message : String(error));
    },
  });

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setFormError(null);

    const trimmedServiceName = serviceName.trim();
    const trimmedVersion = version.trim();

    if (!trimmedServiceName || !trimmedVersion) {
      setFormError("Service name and version are required.");
      return;
    }

    mutation.mutate({
      serviceName: trimmedServiceName,
      version: trimmedVersion,
      environment,
    });
  }

  return (
    <AppShell>
      <div className="mx-auto max-w-2xl">
        <Link
          href="/deployments"
          className="text-sm font-medium text-slate-600 hover:text-slate-900"
        >
          Back to deployments
        </Link>

        <div className="mt-4">
          <h2 className="text-2xl font-bold text-slate-900">
            Create deployment
          </h2>
          <p className="mt-1 text-sm text-slate-500">
            Register a deployment before tracking lifecycle and CI/CD events.
          </p>
        </div>

        <form
          onSubmit={handleSubmit}
          className="mt-6 space-y-5 rounded-lg border border-slate-200 bg-white p-6 shadow-sm"
        >
          <label className="block text-sm font-medium text-slate-700">
            Service name
            <input
              value={serviceName}
              onChange={(event) => setServiceName(event.target.value)}
              placeholder="payment-service"
              className="mt-2 block w-full rounded-lg border border-slate-300 px-3 py-2 text-sm text-slate-900 shadow-sm outline-none focus:border-slate-500"
            />
          </label>

          <label className="block text-sm font-medium text-slate-700">
            Version
            <input
              value={version}
              onChange={(event) => setVersion(event.target.value)}
              placeholder="v1.0.0"
              className="mt-2 block w-full rounded-lg border border-slate-300 px-3 py-2 text-sm text-slate-900 shadow-sm outline-none focus:border-slate-500"
            />
          </label>

          <label className="block text-sm font-medium text-slate-700">
            Environment
            <select
              value={environment}
              onChange={(event) =>
                setEnvironment(event.target.value as DeploymentEnvironment)
              }
              className="mt-2 block w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm text-slate-900 shadow-sm outline-none focus:border-slate-500"
            >
              {environments.map((item) => (
                <option key={item} value={item}>
                  {item}
                </option>
              ))}
            </select>
          </label>

          {formError ? (
            <div className="rounded-lg border border-red-200 bg-red-50 p-4 text-sm text-red-700">
              {formError}
            </div>
          ) : null}

          <div className="flex items-center justify-end gap-3">
            <Link
              href="/deployments"
              className="rounded-lg border border-slate-300 px-4 py-2 text-sm font-medium text-slate-700 hover:bg-slate-50"
            >
              Cancel
            </Link>

            <button
              type="submit"
              disabled={mutation.isPending}
              className="rounded-lg bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-700 disabled:cursor-not-allowed disabled:opacity-60"
            >
              {mutation.isPending ? "Creating..." : "Create deployment"}
            </button>
          </div>
        </form>
      </div>
    </AppShell>
  );
}
