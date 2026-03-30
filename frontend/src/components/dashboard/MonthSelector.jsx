const MONTHS = [
  'Январь','Февраль','Март','Апрель','Май','Июнь',
  'Июль','Август','Сентябрь','Октябрь','Ноябрь','Декабрь',
]

export default function MonthSelector({ year, month, onChange }) {
  const currentYear = new Date().getFullYear()
  const years = Array.from({ length: 5 }, (_, i) => currentYear - i)

  return (
    <div className="flex items-center gap-2">
      {/* Month */}
      <div className="relative">
        <i className="fa-regular fa-calendar-days absolute left-3 top-1/2 -translate-y-1/2 text-secondary text-xs pointer-events-none" />
        <select
          value={month}
          onChange={e => onChange(year, Number(e.target.value))}
          className="appearance-none pl-8 pr-7 py-2.5 text-sm font-medium text-primary
                     bg-surface border border-secondary/25 rounded-2xl shadow-sm
                     hover:border-secondary/50 focus:outline-none focus:ring-2 focus:ring-secondary/30
                     transition-all cursor-pointer"
        >
          {MONTHS.map((m, i) => (
            <option key={i + 1} value={i + 1}>{m}</option>
          ))}
        </select>
        <i className="fa-solid fa-chevron-down absolute right-2.5 top-1/2 -translate-y-1/2 text-muted text-xs pointer-events-none" />
      </div>

      {/* Year */}
      <div className="relative">
        <select
          value={year}
          onChange={e => onChange(Number(e.target.value), month)}
          className="appearance-none px-4 pr-7 py-2.5 text-sm font-medium text-primary
                     bg-surface border border-secondary/25 rounded-2xl shadow-sm
                     hover:border-secondary/50 focus:outline-none focus:ring-2 focus:ring-secondary/30
                     transition-all cursor-pointer"
        >
          {years.map(y => (
            <option key={y} value={y}>{y}</option>
          ))}
        </select>
        <i className="fa-solid fa-chevron-down absolute right-2.5 top-1/2 -translate-y-1/2 text-muted text-xs pointer-events-none" />
      </div>
    </div>
  )
}
