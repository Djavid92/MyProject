const MONTHS = [
  'Январь','Февраль','Март','Апрель','Май','Июнь',
  'Июль','Август','Сентябрь','Октябрь','Ноябрь','Декабрь',
]

export default function MonthSelector({ year, month, onChange }) {
  const currentYear = new Date().getFullYear()
  const years = Array.from({ length: 5 }, (_, i) => currentYear - i)

  return (
    <div className="flex items-center gap-2 flex-wrap">
      <i className="fa-regular fa-calendar text-muted" />
      <select
        className="input !w-auto"
        value={month}
        onChange={e => onChange(year, Number(e.target.value))}
      >
        {MONTHS.map((m, i) => (
          <option key={i + 1} value={i + 1}>{m}</option>
        ))}
      </select>
      <select
        className="input !w-auto"
        value={year}
        onChange={e => onChange(Number(e.target.value), month)}
      >
        {years.map(y => (
          <option key={y} value={y}>{y}</option>
        ))}
      </select>
    </div>
  )
}
