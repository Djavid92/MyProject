export default function NotificationContainer({ notifications, onDismiss }) {
  const icon = {
    success: 'fa-circle-check text-green-600',
    error:   'fa-circle-xmark text-red-600',
    info:    'fa-circle-info text-blue-600',
  }

  return (
    <div className="fixed top-4 right-4 z-50 flex flex-col gap-2 w-80">
      {notifications.map(n => (
        <div
          key={n.id}
          className="animate-slide-down flex items-start gap-3 bg-white rounded-xl shadow-lg border border-secondary/20 p-4"
        >
          <i className={`fa-solid ${icon[n.type] ?? icon.info} mt-0.5 text-lg`} />
          <p className="flex-1 text-sm text-primary">{n.message}</p>
          <button
            onClick={() => onDismiss(n.id)}
            className="text-muted hover:text-primary transition-colors"
          >
            <i className="fa-solid fa-xmark" />
          </button>
        </div>
      ))}
    </div>
  )
}
