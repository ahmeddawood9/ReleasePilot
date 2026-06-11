import type { DeploymentStatus } from "@/types/api";

function statusBadgeClass(status: DeploymentStatus) {
  if (status === "SUCCESS") {
    return "bg-green-50 text-green-700 ring-green-600/20";
  }

  if (status === "FAILED") {
    return "bg-red-50 text-red-700 ring-red-600/20";
  }

  if (status === "RUNNING") {
    return "bg-blue-50 text-blue-700 ring-blue-600/20";
  }

  return "bg-yellow-50 text-yellow-700 ring-yellow-600/20";
}

export function StatusBadge({
  status,
  size = "sm",
}: {
  status: DeploymentStatus;
  size?: "xs" | "sm";
}) {
  const sizeClass = size === "xs" ? "px-2 py-1 text-xs" : "px-3 py-1 text-sm";

  return (
    <span
      className={`inline-flex w-fit rounded-full font-medium ring-1 ring-inset ${sizeClass} ${statusBadgeClass(
        status
      )}`}
    >
      {status}
    </span>
  );
}
