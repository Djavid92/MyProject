const fmt = (n) =>
  new Intl.NumberFormat('ru-RU', { style: 'currency', currency: 'RUB', maximumFractionDigits: 0 }).format(n ?? 0)

export default function StatsCards({ totals, loading }) {
  const balance = (totals?.income ?? 0) - (totals?.expense ?? 0)

  return (
    <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
      <StatCard
        label="Доходы"
        value={fmt(totals?.income)}
        icon="fa-arrow-trend-up"
        iconBg="bg-income/20"
        iconColor="text-income"
        valueColor="text-income"
        loading={loading}
      />
      <StatCard
        label="Расходы"
        value={fmt(totals?.expense)}
        icon="fa-arrow-trend-down"
        iconBg="bg-expense/20"
        iconColor="text-expense"
        valueColor="text-expense"
        loading={loading}
      />
      <StatCard
        label="Баланс"
        value={fmt(balance)}
        icon="fa-scale-balanced"
        iconBg={balance >= 0 ? 'bg-income/20' : 'bg-expense/20'}
        iconColor={balance >= 0 ? 'text-income' : 'text-expense'}
        valueColor={balance >= 0 ? 'text-income' : 'text-expense'}
        loading={loading}
      />
    </div>
  )
}

function StatCard({ label, value, icon, iconBg, iconColor, valueColor, loading }) {
  return (
    <div className="card flex items-center gap-4">
      <div className={`w-12 h-12 rounded-xl flex items-center justify-center flex-shrink-0 ${iconBg}`}>
        <i className={`fa-solid ${icon} text-lg ${iconColor}`} />
      </div>
      <div>
        <p className="text-xs text-muted font-medium uppercase tracking-wide">{label}</p>
        {loading ? (
          <div className="h-6 w-28 bg-secondary/20 rounded animate-pulse mt-1" />
        ) : (
          <p className={`text-xl font-bold ${valueColor}`}>{value}</p>
        )}
      </div>
    </div>
  )
}
