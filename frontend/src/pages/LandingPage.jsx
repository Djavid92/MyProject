import { Link } from 'react-router-dom'
import Header from '../components/layout/Header.jsx'

const FEATURES = [
  { icon: 'fa-arrow-trend-up',    color: 'text-income', bg: 'bg-income/15',   title: 'Учёт доходов',    desc: 'Добавляйте доходы по категориям и отслеживайте источники поступлений' },
  { icon: 'fa-arrow-trend-down',  color: 'text-expense',bg: 'bg-expense/15',  title: 'Учёт расходов',   desc: 'Фиксируйте расходы и контролируйте куда уходят ваши деньги' },
  { icon: 'fa-chart-pie',         color: 'text-secondary',bg:'bg-secondary/15',title: 'Аналитика',       desc: 'Наглядные графики по категориям за любой месяц' },
  { icon: 'fa-tags',              color: 'text-primary', bg: 'bg-primary/10',  title: 'Категории',       desc: 'Создавайте свои категории для удобной группировки операций' },
  { icon: 'fa-calendar-days',     color: 'text-income',  bg: 'bg-income/15',  title: 'Фильтр по месяцу',desc: 'Просматривайте статистику за любой период по вашему выбору' },
  { icon: 'fa-scale-balanced',    color: 'text-expense', bg: 'bg-expense/15', title: 'Баланс',          desc: 'Мгновенно видите текущий баланс: доходы минус расходы' },
]

export default function LandingPage() {
  return (
    <div className="min-h-screen bg-bg flex flex-col">
      <Header />

      {/* Hero */}
      <section className="flex-1 flex flex-col items-center justify-center text-center px-4 py-20">
        <div className="inline-flex items-center gap-2 bg-income/20 text-income rounded-full px-4 py-1.5 text-sm font-medium mb-6">
          <i className="fa-solid fa-wallet" />
          Личные финансы под контролем
        </div>

        <h1 className="text-4xl sm:text-6xl font-bold text-primary leading-tight mb-5">
          Учёт доходов<br />
          <span className="text-secondary">и расходов</span>
        </h1>

        <p className="text-muted text-lg max-w-lg mb-10">
          Простой и удобный инструмент для контроля личного бюджета.
          Добавляйте операции, смотрите аналитику, управляйте категориями.
        </p>

        <div className="flex items-center gap-4 flex-wrap justify-center">
          <Link to="/dashboard" className="btn-primary px-8 py-3 text-base shadow-md">
            <i className="fa-solid fa-chart-pie" />
            Открыть дашборд
          </Link>
          <a href="/swagger-ui.html" target="_blank" rel="noopener noreferrer" className="btn-ghost px-6 py-3 text-base">
            <i className="fa-solid fa-code" />
            Swagger API
          </a>
        </div>
      </section>

      {/* Features */}
      <section className="max-w-5xl mx-auto w-full px-4 sm:px-6 pb-20">
        <h2 className="text-center text-2xl font-bold text-primary mb-10">Возможности</h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {FEATURES.map(f => (
            <div key={f.title} className="card hover:shadow-md transition-shadow">
              <div className={`w-10 h-10 rounded-xl flex items-center justify-center mb-3 ${f.bg}`}>
                <i className={`fa-solid ${f.icon} ${f.color}`} />
              </div>
              <h3 className="font-semibold text-primary mb-1">{f.title}</h3>
              <p className="text-sm text-muted leading-relaxed">{f.desc}</p>
            </div>
          ))}
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-primary text-secondary text-center py-4 text-xs">
        <p>ФинансыПро — учёт личного бюджета</p>
      </footer>
    </div>
  )
}
