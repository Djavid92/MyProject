import { useState } from 'react'
import ConfirmDialog from '../ui/ConfirmDialog.jsx'

export const PAGE_SIZE = 10

const fmt = (n) =>
  new Intl.NumberFormat('ru-RU', { style: 'currency', currency: 'RUB', maximumFractionDigits: 0 }).format(n)

const fmtDate = (d) =>
  new Date(d).toLocaleDateString('ru-RU', { day: '2-digit', month: '2-digit', year: 'numeric' })

export default function TransactionTable({ type, items, onDelete, onEdit, loading, page }) {
  const isIncome   = type === 'income'
  const label      = isIncome ? 'Доходы' : 'Расходы'
  const colorClass = isIncome ? 'text-income' : 'text-expense'
  const badgeClass = isIncome ? 'badge-income' : 'badge-expense'
  const icon       = isIncome ? 'fa-arrow-trend-up' : 'fa-arrow-trend-down'

  const [confirmId, setConfirmId] = useState(null)

  const sorted  = [...(items ?? [])].sort((a, b) => new Date(b.date) - new Date(a.date))
  const start   = (page - 1) * PAGE_SIZE
  const visible = sorted.slice(start, start + PAGE_SIZE)

  return (
    <div className="card">
      {/* Table header */}
      <div className="flex items-center gap-2 mb-4">
        <i className={`fa-solid ${icon} ${colorClass}`} />
        <h3 className={`text-sm font-semibold uppercase tracking-wide ${colorClass}`}>{label}</h3>
        <span className="ml-auto text-xs text-muted bg-secondary/10 px-2 py-0.5 rounded-full">
          {items?.length ?? 0} записей
        </span>
      </div>

      {loading ? (
        <Skeleton />
      ) : !items?.length ? (
        <Empty />
      ) : (
        <div className="overflow-x-auto -mx-1">
          <table className="w-full text-sm">
            <thead>
              <tr className="text-left text-xs text-muted uppercase tracking-wide border-b border-secondary/20">
                <th className="pb-2 px-1 font-medium">Название</th>
                <th className="pb-2 px-1 font-medium">Категория</th>
                <th className="pb-2 px-1 font-medium">Сумма</th>
                <th className="pb-2 px-1 font-medium">Дата</th>
                <th className="pb-2 px-1 font-medium">Описание</th>
                <th className="pb-2 px-1" />
              </tr>
            </thead>
            <tbody>
              {visible.map((item, i) => (
                <tr
                  key={item.id}
                  className={`border-b border-secondary/10 hover:bg-secondary/5 transition-colors
                    ${i % 2 !== 0 ? 'bg-secondary/5' : ''}`}
                >
                  <td className="py-2 px-1 font-medium text-primary">{item.name}</td>
                  <td className="py-2 px-1">
                    <span className={badgeClass}>{item.category?.name ?? '—'}</span>
                  </td>
                  <td className={`py-2 px-1 font-semibold ${colorClass}`}>
                    {isIncome ? '+' : '−'}{fmt(item.amount)}
                  </td>
                  <td className="py-2 px-1 text-muted">{fmtDate(item.date)}</td>
                  <td className="py-2 px-1 text-muted max-w-[150px] truncate">
                    {item.description || '—'}
                  </td>
                  <td className="py-2 px-1">
                    <div className="flex items-center gap-1">
                      <button
                        onClick={() => onEdit(item)}
                        className="w-7 h-7 flex items-center justify-center rounded-lg text-muted
                                   hover:bg-secondary/20 hover:text-primary transition-colors"
                        title="Редактировать"
                      >
                        <i className="fa-solid fa-pen text-xs" />
                      </button>
                      <button
                        onClick={() => setConfirmId(item.id)}
                        className="w-7 h-7 flex items-center justify-center rounded-lg text-muted
                                   hover:bg-expense/20 hover:text-expense transition-colors"
                        title="Удалить"
                      >
                        <i className="fa-solid fa-trash-can text-xs" />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <ConfirmDialog
        open={confirmId !== null}
        onClose={() => setConfirmId(null)}
        onConfirm={() => onDelete(confirmId)}
        title="Удалить запись"
        message="Вы уверены, что хотите удалить эту запись? Это действие необратимо."
        danger
      />
    </div>
  )
}

function Skeleton() {
  return (
    <div className="space-y-2">
      {[...Array(4)].map((_, i) => (
        <div key={i} className="h-9 bg-secondary/10 rounded-lg animate-pulse" />
      ))}
    </div>
  )
}

function Empty() {
  return (
    <div className="py-8 flex flex-col items-center gap-2 text-muted">
      <i className="fa-regular fa-folder-open text-3xl opacity-40" />
      <p className="text-sm">Нет операций за этот месяц</p>
    </div>
  )
}
