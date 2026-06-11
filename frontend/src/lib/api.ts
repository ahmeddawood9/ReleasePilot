import type {
  CreateDeploymentRequest,
  DashboardSummary,
  DeploymentEventResponse,
  DeploymentResponse,
  PageResponse,
} from "@/types/api";

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

export class ApiError extends Error {
  status: number;

  constructor(message: string, status: number) {
    super(message);
    this.name = "ApiError";
    this.status = status;
  }
}

async function getErrorMessage(response: Response) {
  const fallback = `API request failed: ${response.status}`;

  try {
    const body = (await response.json()) as { message?: string; error?: string };

    return body.message ?? body.error ?? fallback;
  } catch {
    return fallback;
  }
}

async function apiFetch<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, init);

  if (!response.ok) {
    throw new ApiError(await getErrorMessage(response), response.status);
  }

  return response.json() as Promise<T>;
}

export function getDashboardSummary(): Promise<DashboardSummary> {
  return apiFetch<DashboardSummary>("/api/dashboard/summary");
}

export function getDeployments(params?: {
  page?: number;
  size?: number;
  status?: string;
  environment?: string;
}): Promise<PageResponse<DeploymentResponse>> {
  const searchParams = new URLSearchParams();

  if (params?.page !== undefined) {
    searchParams.set("page", String(params.page));
  }

  if (params?.size !== undefined) {
    searchParams.set("size", String(params.size));
  }

  if (params?.status) {
    searchParams.set("status", params.status);
  }

  if (params?.environment) {
    searchParams.set("environment", params.environment);
  }

  const query = searchParams.toString();

  return apiFetch<PageResponse<DeploymentResponse>>(
    `/api/deployments${query ? `?${query}` : ""}`
  );
}

export function getDeployment(id: number): Promise<DeploymentResponse> {
  return apiFetch<DeploymentResponse>(`/api/deployments/${id}`);
}

export function getDeploymentEvents(
  id: number
): Promise<DeploymentEventResponse[]> {
  return apiFetch<DeploymentEventResponse[]>(`/api/deployments/${id}/events`);
}

export function createDeployment(
  request: CreateDeploymentRequest
): Promise<DeploymentResponse> {
  return apiFetch<DeploymentResponse>("/api/deployments", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(request),
  });
}

export function startDeployment(id: number): Promise<DeploymentResponse> {
  return apiFetch<DeploymentResponse>(`/api/deployments/${id}/start`, {
    method: "PATCH",
  });
}

export function markDeploymentSuccess(id: number): Promise<DeploymentResponse> {
  return apiFetch<DeploymentResponse>(`/api/deployments/${id}/success`, {
    method: "PATCH",
  });
}

export function markDeploymentFailed(id: number): Promise<DeploymentResponse> {
  return apiFetch<DeploymentResponse>(`/api/deployments/${id}/fail`, {
    method: "PATCH",
  });
}
