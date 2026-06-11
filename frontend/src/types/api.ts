export type DeploymentStatus = "PENDING" | "RUNNING" | "SUCCESS" | "FAILED";

export type DeploymentEnvironment = "DEV" | "STAGING" | "PRODUCTION";

export type DashboardSummary = {
  totalDeployments: number;
  pendingDeployments: number;
  runningDeployments: number;
  successfulDeployments: number;
  failedDeployments: number;
  devDeployments: number;
  stagingDeployments: number;
  productionDeployments: number;
};

export type DeploymentResponse = {
  id: number;
  serviceName: string;
  version: string;
  environment: DeploymentEnvironment;
  status: DeploymentStatus;
  createdAt: string;
  startedAt: string | null;
  completedAt: string | null;
};

export type CreateDeploymentRequest = {
  serviceName: string;
  version: string;
  environment: DeploymentEnvironment;
};

export type DeploymentEventResponse = {
  id: number;
  deploymentId: number;
  status: DeploymentStatus;
  message: string | null;
  occurredAt: string;
  createdAt: string;
  provider: string | null;
  externalDeploymentId: string | null;
  commitSha: string | null;
  branchName: string | null;
  triggeredBy: string | null;
  deploymentUrl: string | null;
};

export type PageResponse<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
};
