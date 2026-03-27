import { Link, useLocation } from 'react-router-dom'

export default function Header() {
  const { pathname } = useLocation()

  return (
    <header className="bg-primary text-surface shadow-lg sticky top-0 z-30">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Logo */}
          <Link to="/" className="flex items-center gap-3 group">
            <div className="w-9 h-9 bg-income rounded-xl flex items-center justify-center shadow-sm group-hover:scale-105 transition-transform">
              <i className="fa-solid fa-wallet text-white text-sm" />
            </div>
            <div>
              <p className="font-semibold text-sm leading-none">ФинансыПро</p>
              <p className="text-secondary text-xs leading-none mt-0.5">Учёт доходов и расходов</p>
            </div>
          </Link>

          {/* Nav */}
          <nav className="flex items-center gap-1">
            <NavLink to="/" active={pathname === '/'}>
              <i className="fa-solid fa-house" /> Главная
            </NavLink>
            <NavLink to="/dashboard" active={pathname === '/dashboard'}>
              <i className="fa-solid fa-chart-pie" /> Дашборд
            </NavLink>
          </nav>
        </div>
      </div>
    </header>
  )
}

function NavLink({ to, active, children }) {
  return (
    <Link
      to={to}
      className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-sm font-medium transition-colors
        ${active
          ? 'bg-income/30 text-surface'
          : 'text-secondary hover:bg-white/10 hover:text-surface'
        }`}
    >
      {children}
    </Link>
  )
}
