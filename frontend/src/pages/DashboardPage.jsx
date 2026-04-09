import { useState, useEffect, useCallback, useRef } from 'react'
import {
  getTotalsByMonth, getOperationsByMonth,
  getCategories, addCategory, deleteCategory,
  addIncome, updateIncome, deleteIncome,
  addExpense, updateExpense, deleteExpense,
  getOperationsByIncomeCategory, getOperationsByExpenseCategory,
} from '../api/financeApi.js'

import StatsCards         from '../components/dashboard/StatsCards.jsx'
import CategoryPieChart   from '../components/dashboard/CategoryPieChart.jsx'
import TransactionTable, { PAGE_SIZE } from '../components/dashboard/TransactionTable.jsx'
import AddTransactionModal from '../components/dashboard/AddTransactionModal.jsx'
import CategoryManager    from '../components/dashboard/CategoryManager.jsx'
import MonthSelector      from '../components/dashboard/MonthSelector.jsx'
import NotificationContainer from '../components/ui/NotificationContainer.jsx'
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

  const [modal, setModal]         = useState(null)  // 'income' | 'expense' | null
  const [editItem, setEditItem]   = useState(null)  // { type: 'income'|'expense', item }
  const [activeTab, setTab]       = useState('income') // 'income' | 'expense' | 'categories'
  const [page, setPage]           = useState(1)

  const [catDropdownOpen, setCatDropdownOpen] = useState(false)
  const catDropdownRef = useRef(null)

  const [incomeFilter, setIncomeFilter]         = useState(null)  // active category name or null
  const [expenseFilter, setExpenseFilter]       = useState(null)
  const [filteredIncomes, setFilteredIncomes]   = useState(null)
  const [filteredExpenses, setFilteredExpenses] = useState(null)

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
      setPage(1)
      setIncomeFilter(null)
      setFilteredIncomes(null)
      setExpenseFilter(null)
      setFilteredExpenses(null)
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

  useEffect(() => {
    const handler = (e) => {
      if (catDropdownRef.current && !catDropdownRef.current.contains(e.target)) {
        setCatDropdownOpen(false)
      }
    }
    document.addEventListener('mousedown', handler)
    return () => document.removeEventListener('mousedown', handler)
  }, [])

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

  const handleUpdateIncome = async (id, dto) => {
    await updateIncome(id, dto)
    notify('Доход обновлён', 'success')
    fetchOps()
  }

  const handleUpdateExpense = async (id, dto) => {
    await updateExpense(id, dto)
    notify('Расход обновлён', 'success')
    fetchOps()
  }

  const handleFilterIncomeCategory = async (categoryName) => {
    if (categoryName === null) {
      setIncomeFilter(null)
      setFilteredIncomes(null)
      setPage(1)
      return
    }
    try {
      const data = await getOperationsByIncomeCategory(categoryName)
      setFilteredIncomes(data)
      setIncomeFilter(categoryName)
      setPage(1)
    } catch {
      notify('Не удалось загрузить данные', 'error')
    }
  }

  const handleFilterExpenseCategory = async (categoryName) => {
    if (categoryName === null) {
      setExpenseFilter(null)
      setFilteredExpenses(null)
      setPage(1)
      return
    }
    try {
      const data = await getOperationsByExpenseCategory(categoryName)
      setFilteredExpenses(data)
      setExpenseFilter(categoryName)
      setPage(1)
    } catch {
      notify('Не удалось загрузить данные', 'error')
    }
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

          {/* Category filter — visible only on income/expense tabs */}
          {(activeTab === 'income' || activeTab === 'expense') && categories.length > 0 && (
            <div className="relative" ref={catDropdownRef}>
              {(() => {
                const isIncome       = activeTab === 'income'
                const activeFilter   = isIncome ? incomeFilter : expenseFilter
                const onFilter       = isIncome ? handleFilterIncomeCategory : handleFilterExpenseCategory
                const activeBg       = isIncome ? 'bg-income' : 'bg-expense'
                return (
                  <>
                    <button
                      onClick={() => setCatDropdownOpen(v => !v)}
                      className={`inline-flex items-center gap-1.5 text-sm font-medium px-3 py-2.5 rounded-2xl shadow-sm transition-all
                        ${activeFilter
                          ? `${activeBg} text-white hover:brightness-105`
                          : 'bg-surface border border-secondary/25 text-primary hover:border-secondary/50'
                        }`}
                    >
                      <i className="fa-solid fa-filter text-xs" />
                      {activeFilter ?? 'Категория'}
                      <i className={`fa-solid fa-chevron-down text-xs transition-transform ${catDropdownOpen ? 'rotate-180' : ''}`} />
                    </button>

                    {catDropdownOpen && (
                      <div className="absolute right-0 top-full mt-1 z-20 min-w-[160px] bg-surface border border-secondary/20 rounded-xl shadow-lg flex flex-col overflow-hidden">
                        <button
                          onClick={() => { onFilter(null); setCatDropdownOpen(false) }}
                          className={`shrink-0 w-full text-left text-xs px-3 py-2 transition-colors border-b border-secondary/10
                            ${!activeFilter ? `${activeBg} text-white` : 'text-primary hover:bg-secondary/10'}`}
                        >
                          Все категории
                        </button>
                        <div className="overflow-y-auto max-h-48">
                          {categories.map(cat => (
                            <button
                              key={cat.id}
                              onClick={() => { onFilter(cat.name); setCatDropdownOpen(false) }}
                              className={`w-full text-left text-xs px-3 py-2 transition-colors
                                ${activeFilter === cat.name
                                  ? `${activeBg} text-white`
                                  : 'text-primary hover:bg-secondary/10'
                                }`}
                            >
                              {cat.name}
                            </button>
                          ))}
                        </div>
                      </div>
                    )}
                  </>
                )
              })()}
            </div>
          )}
          <button
            onClick={() => setModal('income')}
            className="inline-flex items-center gap-2.5 px-5 py-2.5 rounded-2xl
                       bg-income text-white text-sm font-semibold
                       shadow-sm hover:shadow-md hover:brightness-105 active:scale-95 transition-all"
          >
            <span className="w-5 h-5 flex items-center justify-center bg-white/20 rounded-lg">
              <i className="fa-solid fa-plus text-xs" />
            </span>
            Доход
          </button>
          <button
            onClick={() => setModal('expense')}
            className="inline-flex items-center gap-2.5 px-5 py-2.5 rounded-2xl
                       bg-expense text-white text-sm font-semibold
                       shadow-sm hover:shadow-md hover:brightness-105 active:scale-95 transition-all"
          >
            <span className="w-5 h-5 flex items-center justify-center bg-white/20 rounded-lg">
              <i className="fa-solid fa-minus text-xs" />
            </span>
            Расход
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
        <div className="flex items-center border-b border-secondary/20 bg-surface">
          {[
            { key: 'income',     label: 'Доходы',    icon: 'fa-arrow-trend-up',   count: incomes.length },
            { key: 'expense',    label: 'Расходы',   icon: 'fa-arrow-trend-down', count: expenses.length },
            { key: 'categories', label: 'Категории', icon: 'fa-tags',             count: categories.length },
          ].map(tab => (
            <button
              key={tab.key}
              onClick={() => { setTab(tab.key); setPage(1) }}
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

          {/* Pagination numbers */}
          {(() => {
            const list = activeTab === 'income' ? incomes : activeTab === 'expense' ? expenses : null
            if (!list) return null
            const total = Math.ceil(list.length / PAGE_SIZE)
            if (total <= 1) return null

            const pages = []
            for (let p = 1; p <= total; p++) {
              const showDot =
                (p === 2 && page > 3) ||
                (p === total - 1 && page < total - 2)
              const show =
                p === 1 || p === total ||
                Math.abs(p - page) <= 1

              if (showDot) { pages.push({ p, dot: true }); continue }
              if (!show) continue
              pages.push({ p, dot: false })
            }

            return (
              <div className="ml-auto flex items-center gap-1 pr-3">
                {pages.map(({ p, dot }) =>
                  dot ? (
                    <span key={`dot-${p}`} className="w-7 text-center text-xs text-muted select-none">…</span>
                  ) : (
                    <button
                      key={p}
                      onClick={() => setPage(p)}
                      className={`w-7 h-7 text-xs rounded-lg font-medium transition-colors
                        ${page === p
                          ? 'bg-secondary text-white'
                          : 'text-muted hover:bg-secondary/15 hover:text-primary'
                        }`}
                    >
                      {p}
                    </button>
                  )
                )}
              </div>
            )
          })()}
        </div>

        {/* Tab content */}
        <div className="p-5">
          {activeTab === 'income' && (
            <TransactionTable
              type="income"
              items={filteredIncomes ?? incomes}
              categories={categories}
              activeCategoryFilter={incomeFilter}
              onCategoryFilter={handleFilterIncomeCategory}
              onDelete={handleDeleteIncome}
              onEdit={(item) => setEditItem({ type: 'income', item })}
              loading={loadingOps}
              page={page}
            />
          )}
          {activeTab === 'expense' && (
            <TransactionTable
              type="expense"
              items={filteredExpenses ?? expenses}
              categories={categories}
              activeCategoryFilter={expenseFilter}
              onCategoryFilter={handleFilterExpenseCategory}
              onDelete={handleDeleteExpense}
              onEdit={(item) => setEditItem({ type: 'expense', item })}
              loading={loadingOps}
              page={page}
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

      {/* Modals — добавление */}
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

      {/* Modals — редактирование */}
      <AddTransactionModal
        type="income"
        open={editItem?.type === 'income'}
        onClose={() => setEditItem(null)}
        categories={categories}
        onAddCategory={handleAddCategory}
        editItem={editItem?.item}
        onUpdate={handleUpdateIncome}
      />
      <AddTransactionModal
        type="expense"
        open={editItem?.type === 'expense'}
        onClose={() => setEditItem(null)}
        categories={categories}
        onAddCategory={handleAddCategory}
        editItem={editItem?.item}
        onUpdate={handleUpdateExpense}
      />
    </>
  )
}
