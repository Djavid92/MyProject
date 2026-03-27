import { useState } from 'react'
import ConfirmDialog from '../ui/ConfirmDialog.jsx'

export default function CategoryManager({ categories, onAdd, onDelete, loading }) {
  const [name, setName]       = useState('')
  const [submitting, setSub]  = useState(false)
  const [error, setError]     = useState('')
  const [confirmId, setConf]  = useState(null)

  const handleAdd = async (e) => {
    e.preventDefault()
    const trimmed = name.trim()
    if (!trimmed) return setError('Введите название')
    if (trimmed.length > 20) return setError('Максимум 20 символов')
    setError('')
    setSub(true)
    try {
      await onAdd({ name: trimmed })
      setName('')
    } catch (err) {
      setError(err?.response?.data?.message ?? 'Ошибка при создании')
    } finally {
      setSub(false)
    }
  }

  return (
    <div className="card">
      <h3 className="text-sm font-semibold text-muted uppercase tracking-wide mb-4">
        <i className="fa-solid fa-tags mr-2" />Категории
      </h3>

      {/* Add form */}
      <form onSubmit={handleAdd} className="flex gap-2 mb-4">
        <input
          className="input flex-1"
          placeholder="Название категории..."
          maxLength={20}
          value={name}
          onChange={e => { setName(e.target.value); setError('') }}
        />
        <button type="submit" disabled={submitting} className="btn-primary flex-shrink-0">
          {submitting
            ? <i className="fa-solid fa-spinner animate-spin" />
            : <i className="fa-solid fa-plus" />
          }
          Добавить
        </button>
      </form>
      {error && <p className="text-xs text-expense mb-3">{error}</p>}

      {/* List */}
      {loading ? (
        <div className="space-y-2">
          {[...Array(3)].map((_, i) => (
            <div key={i} className="h-8 bg-secondary/10 rounded animate-pulse" />
          ))}
        </div>
      ) : !categories?.length ? (
        <p className="text-sm text-muted text-center py-4">Категорий ещё нет</p>
      ) : (
        <div className="flex flex-wrap gap-2">
          {categories.map(c => (
            <div
              key={c.id}
              className="flex items-center gap-1.5 bg-secondary/10 rounded-lg px-3 py-1 text-sm"
            >
              <span className="text-primary font-medium">{c.name}</span>
              <button
                onClick={() => setConf(c.id)}
                className="text-muted hover:text-expense transition-colors ml-1"
                title="Удалить категорию"
              >
                <i className="fa-solid fa-xmark text-xs" />
              </button>
            </div>
          ))}
        </div>
      )}

      <ConfirmDialog
        open={confirmId !== null}
        onClose={() => setConf(null)}
        onConfirm={() => onDelete(confirmId)}
        title="Удалить категорию"
        message="Удалить эту категорию? Связанные операции останутся без категории."
        danger
      />
    </div>
  )
}
