import { Pie } from 'react-chartjs-2'
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js'

ChartJS.register(ArcElement, Tooltip, Legend)

const INCOME_COLORS  = ['#B88D6A','#C9A882','#DAC39A','#9D7855','#8A6642','#EBDEC2']
const EXPENSE_COLORS = ['#A05035','#B86040','#CC7050','#8A4025','#7A3020','#D08070']

function buildChartData(items, colors) {
  if (!items?.length) return null
  const grouped = {}
  items.forEach(item => {
    const name = item.category?.name ?? 'Без категории'
    grouped[name] = (grouped[name] ?? 0) + Number(item.amount)
  })
  const labels = Object.keys(grouped)
  return {
    labels,
    datasets: [{
      data: Object.values(grouped),
      backgroundColor: colors.slice(0, labels.length),
      borderWidth: 2,
      borderColor: '#E8DFCF',
    }],
  }
}

const OPTIONS = {
  plugins: {
    legend: {
      position: 'bottom',
      labels: { font: { size: 12 }, color: '#3F3F2C', padding: 12, boxWidth: 12 },
    },
    tooltip: {
      callbacks: {
        label: (ctx) => {
          const val = ctx.parsed
          const total = ctx.dataset.data.reduce((a, b) => a + b, 0)
          const pct = ((val / total) * 100).toFixed(1)
          return ` ${new Intl.NumberFormat('ru-RU', { style: 'currency', currency: 'RUB', maximumFractionDigits: 0 }).format(val)} (${pct}%)`
        },
      },
    },
  },
}

export default function CategoryPieChart({ incomes, expenses, loading }) {
  const incomeData  = buildChartData(incomes, INCOME_COLORS)
  const expenseData = buildChartData(expenses, EXPENSE_COLORS)

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
      <ChartCard title="Доходы по категориям" data={incomeData} loading={loading} empty={!incomes?.length} />
      <ChartCard title="Расходы по категориям" data={expenseData} loading={loading} empty={!expenses?.length} />
    </div>
  )
}

function ChartCard({ title, data, loading, empty }) {
  return (
    <div className="card">
      <h3 className="text-sm font-semibold text-muted uppercase tracking-wide mb-4">{title}</h3>
      {loading ? (
        <div className="h-48 flex items-center justify-center">
          <div className="w-8 h-8 border-4 border-secondary/30 border-t-secondary rounded-full animate-spin" />
        </div>
      ) : empty || !data ? (
        <div className="h-48 flex flex-col items-center justify-center gap-2 text-muted">
          <i className="fa-regular fa-chart-pie text-3xl opacity-40" />
          <p className="text-sm">Нет данных за этот месяц</p>
        </div>
      ) : (
        <div className="h-52">
          <Pie data={data} options={{ ...OPTIONS, maintainAspectRatio: false }} />
        </div>
      )}
    </div>
  )
}
