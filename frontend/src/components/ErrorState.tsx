type ErrorStateProps = {
  title: string;
  message?: string;
  onRetry?: () => void;
};

export function ErrorState({ title, message, onRetry }: ErrorStateProps) {
  return (
    <div className="rounded-lg border border-red-200 bg-red-50 p-4 text-red-700">
      <p className="text-sm font-semibold">{title}</p>

      {message ? <p className="mt-1 text-sm">{message}</p> : null}

      {onRetry ? (
        <button
          type="button"
          onClick={onRetry}
          className="mt-3 rounded-lg border border-red-200 bg-white px-3 py-2 text-sm font-medium text-red-700 hover:bg-red-50"
        >
          Retry
        </button>
      ) : null}
    </div>
  );
}
