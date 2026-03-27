import { useState, useEffect, useCallback } from 'react'
import {
  getTotalsByMonth, getOperationsByMonth,
  getCategories, addCategory, deleteCategory,
  addIncome, deleteIncome,
  addExpense, deleteExpense,
  deleteAll,
} from '../api/financeApi.js'

import StatsCards         from '../components/dashboard/StatsCards.jsx'
import CategoryPieChart   from '../components/dashboard/CategoryPieChart.jsx'
import TransactionTable   from '../components/dashboard/TransactionTable.jsx'
import AddTransactionModal from '../components/dashboard/AddTransactionModal.jsx'
import CategoryManager    from '../components/dashboard/CategoryManager.jsx'
import MonthSelector      from '../components/dashboard/MonthSelector.jsx'
import NotificationContainer from '../components/ui/NotificationContainer.jsx'
import ConfirmDialog      from '../components/ui/ConfirmDialog.jsx'
import { useNotification } from '../hooks/useNotification.js'

function currentPeriod() {
  const d = new Date()
  return { year: d.getFullYear(), month: d.getMonth() + 1 }
}

export default function DashboardPage() {
  const { notifications, notify, dismiss } = useNotification()

  const [period, setPeriod]         = useState(currentPeriod())
  const [totals, setTotals]         = useState(null)
  const [incomes, setIncomes]       = useState([])
  const [expenses, setExpenses]     = useState([])
  const [categories, setCategories] = useState([])

  const [loadingOps, setLoadingOps]   = useState(false)
  const [loadingCats, setLoadingCats] = useState(false)

  const [modal, setModal]       = useState(null) // 'income' | 'expense' | null
  const [showDeleteAll, setDA]  = useState(false)
  const [activeTab, setTab]     = useState('income') // 'income' | 'expense' | 'categories'

  // ── Fetch data ────────────────────────────────────────────────────────────
  const fetchOps = useCallback(async () => {
    setLoadingOps(true)
    try {
      const [t, ops] = await Promise.all([
        getTotalsByMonth(period.year, period.month),
        getOperationsByMonth(period.year, period.month),
      ])
      setTotals(t)
      setIncomes(ops.incomes ?? [])
      setExpenses(ops.expenses ?? [])
    } catch {
      notify('Не удалось загрузить данные', 'error')
    } finally {
      setLoadingOps(false)
    }
  }, [period])

  const fetchCats = useCallback(async () => {
    setLoadingCats(true)
    try {
      setCategories(await getCategories())
    } catch {
      notify('Не удалось загрузить категории', 'error')
    } finally {
      setLoadingCats(false)
    }
  }, [])

  useEffect(() => { fetchOps() }, [fetchOps])
  useEffect(() => { fetchCats() }, [fetchCats])

  // ── Handlers ──────────────────────────────────────────────────────────────
  const handleAddIncome = async (dto) => {
    await addIncome(dto)
    notify('Доход добавлен', 'success')
    fetchOps()
  }

  const handleAddExpense = async (dto) => {
    await addExpense(dto)
    notify('Расход добавлен', 'success')
    fetchOps()
  }

  const handleDeleteIncome = async (id) => {
    try { await deleteIncome(id); notify('Доход удалён', 'success'); fetchOps() }
    catch { notify('Не удалось удалить', 'error') }
  }

  const handleDeleteExpense = async (id) => {
    try { await deleteExpense(id); notify('Расход удалён', 'success'); fetchOps() }
    catch { notify('Не удалось удалить', 'error') }
  }

  const handleAddCategory = async (dto) => {
    const created = await addCategory(dto)
    notify('Категория создана', 'success')
    fetchCats()
    return created
  }

  const handleDeleteCategory = async (id) => {
    try { await deleteCategory(id); notify('Категория удалена', 'success'); fetchCats() }
    catch { notify('Не удалось удалить категорию', 'error') }
  }

  const handleDeleteAll = async () => {
    try { await deleteAll(); notify('Все записи удалены', 'info'); fetchOps() }
    catch { notify('Не удалось удалить записи', 'error') }
  }

  // ── Render ────────────────────────────────────────────────────────────────
  return (
    <>
      <NotificationContainer notifications={notifications} onDismiss={dismiss} />

      {/* Page header */}
      <div className="flex flex-wrap items-center justify-between gap-4 mb-6">
        <div>
          <h1 className="text-2xl font-bold text-primary">Дашборд</h1>
          <p className="text-sm text-muted">Обзор финансов за выбранный месяц</p>
        </div>
        <div className="flex items-center gap-2 flex-wrap">
          <MonthSelector
            year={period.year}
            month={period.month}
            onChange={(y, m) => setPeriod({ year: y, month: m })}
          />
          <button className="btn-income" onClick={() => setModal('income')}>
            <i className="fa-solid fa-plus" /> Доход
          </button>
          <button className="btn-expense" onClick={() => setModal('expense')}>
            <i className="fa-solid fa-minus" /> Расход
          </button>
        </div>
      </div>

      {/* Stats */}
      <div className="mb-6">
        <StatsCards totals={totals} loading={loadingOps} />
      </div>

      {/* Charts */}
      <div className="mb-6">
        <CategoryPieChart incomes={incomes} expenses={expenses} loading={loadingOps} />
      </div>

      {/* Tabs */}
      <div className="card mb-0 p-0 overflow-hidden">
        {/* Tab bar */}
        <div className="flex border-b border-secondary/20 bg-surface">
          {[
            { key: 'income',     label: 'Доходы',    icon: 'fa-arrow-trend-up',   count: incomes.length },
            { key: 'expense',    label: 'Расходы',   icon: 'fa-arrow-trend-down', count: expenses.length },
            { key: 'categories', label: 'Категории', icon: 'fa-tags',             count: categories.length },
          ].map(tab => (
            <button
              key={tab.key}
              onClick={() => setTab(tab.key)}
              className={`flex items-center gap-2 px-5 py-3 text-sm font-medium border-b-2 transition-colors
                ${activeTab === tab.key
                  ? 'border-secondary text-primary'
                  : 'border-transparent text-muted hover:text-primary hover:border-secondary/40'
                }`}
            >
              <i className={`fa-solid ${tab.icon}`} />
              {tab.label}
              <span className="text-xs bg-secondary/15 px-1.5 py-0.5 rounded-full">{tab.count}</span>
            </button>
          ))}

          {/* Delete all */}
          <button
            onClick={() => setDA(true)}
            className="ml-auto flex items-center gap-1.5 px-4 py-3 text-xs text-muted hover:text-expense transition-colors"
          >
            <i className="fa-solid fa-trash" /> Очистить всё
          </button>
        </div>

        {/* Tab content */}
        <div className="p-5">
          {activeTab === 'income' && (
            <TransactionTable
              type="income"
              items={incomes}
              onDelete={handleDeleteIncome}
              loading={loadingOps}
            />
          )}
          {activeTab === 'expense' && (
            <TransactionTable
              type="expense"
              items={expenses}
              onDelete={handleDeleteExpense}
              loading={loadingOps}
            />
          )}
          {activeTab === 'categories' && (
            <CategoryManager
              categories={categories}
              onAdd={handleAddCategory}
              onDelete={handleDeleteCategory}
              loading={loadingCats}
            />
          )}
        </div>
      </div>

      {/* Modals */}
      <AddTransactionModal
        type="income"
        open={modal === 'income'}
        onClose={() => setModal(null)}
        categories={categories}
        onSubmit={handleAddIncome}
        onAddCategory={handleAddCategory}
      />
      <AddTransactionModal
        type="expense"
        open={modal === 'expense'}
        onClose={() => setModal(null)}
        categories={categories}
        onSubmit={handleAddExpense}
        onAddCategory={handleAddCategory}
      />
      <ConfirmDialog
        open={showDeleteAll}
        onClose={() => setDA(false)}
        onConfirm={handleDeleteAll}
        title="Удалить все записи"
        message="Это удалит ВСЕ доходы и расходы навсегда. Действие нельзя отменить."
        danger
      />
    </>
  )
}
