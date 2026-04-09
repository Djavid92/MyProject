import { useState, useEffect, useRef } from 'react'
import Modal from '../ui/Modal.jsx'
import CalculatorModal from './CalculatorModal.jsx'

function CategorySelect({ categories, value, onChange }) {
  const [open, setOpen] = useState(false)
  const ref = useRef(null)

  const openUp = categories.length < 10

  const selected = categories.find(c => String(c.id) === String(value))
  const label = selected ? selected.name : '— без категории —'

  useEffect(() => {
    const handleClick = (e) => {
      if (ref.current && !ref.current.contains(e.target)) setOpen(false)
    }
    document.addEventListener('mousedown', handleClick)
    return () => document.removeEventListener('mousedown', handleClick)
  }, [])

  const pick = (val) => { onChange(val); setOpen(false) }

  return (
    <div className="relative" ref={ref}>
      <button
        type="button"
        onClick={() => setOpen(v => !v)}
        className="input w-full flex items-center justify-between text-left pr-7"
      >
        <span className={selected ? '' : 'text-muted'}>{label}</span>
        <i className={`fa-solid fa-chevron-down absolute right-2.5 top-1/2 -translate-y-1/2 text-muted text-xs transition-transform ${open ? 'rotate-180' : ''}`} />
      </button>
      {open && (
        <ul className={`absolute z-50 w-full bg-surface border border-border rounded-xl shadow-lg overflow-y-auto max-h-48 ${openUp ? 'bottom-full mb-1' : 'top-full mt-1'}`}>
          <li
            className="px-3 py-2 text-sm text-muted cursor-pointer hover:bg-secondary/20 rounded-t-xl"
            onClick={() => pick('')}
          >
            — без категории —
          </li>
          {categories.map(c => (
            <li
              key={c.id}
              className={`px-3 py-2 text-sm cursor-pointer hover:bg-secondary/20 last:rounded-b-xl ${String(c.id) === String(value) ? 'text-primary font-medium' : ''}`}
              onClick={() => pick(String(c.id))}
            >
              {c.name}
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}

const today = () => new Date().toISOString().split('T')[0]

const empty = () => ({ name: '', categoryId: '', amount: '', description: '', date: today() })

export default function AddTransactionModal({ type, open, onClose, categories, onSubmit, onAddCategory, editItem, onUpdate }) {
  const isIncome  = type === 'income'
  const isEdit    = !!editItem
  const [form, setForm]       = useState(empty())
  const [loading, setLoading] = useState(false)
  const [error, setError]     = useState('')
  const [success, setSuccess] = useState(false)

  useEffect(() => {
    if (editItem) {
      setForm({
        name:        editItem.name        ?? '',
        categoryId:  editItem.category?.id ? String(editItem.category.id) : '',
        amount:      editItem.amount      ?? '',
        description: editItem.description ?? '',
        date:        editItem.date        ?? today(),
      })
      setError('')
      setSuccess(false)
    }
  }, [editItem])

  // inline new-category state
  const [showNewCat, setShowNewCat] = useState(false)
  const [newCatName, setNewCatName] = useState('')
  const [catLoading, setCatLoading] = useState(false)
  const [catError, setCatError]     = useState('')

  const [showCalc, setShowCalc] = useState(false)

  const set = (k, v) => setForm(f => ({ ...f, [k]: v }))

  const reset = () => {
    setForm(empty())
    setError('')
    setSuccess(false)
    setShowNewCat(false)
    setNewCatName('')
    setCatError('')
    setShowCalc(false)
  }

  const handleClose = () => { reset(); onClose() }

  const handleCreateCategory = async () => {
    const trimmed = newCatName.trim()
    if (!trimmed) return setCatError('Введите название')
    if (trimmed.length > 20) return setCatError('Максимум 20 символов')
    setCatError('')
    setCatLoading(true)
    try {
      const created = await onAddCategory({ name: trimmed })
      if (created?.id) set('categoryId', String(created.id))
      setNewCatName('')
      setShowNewCat(false)
    } catch (err) {
      setCatError(err?.response?.data?.message ?? 'Ошибка при создании')
    } finally {
      setCatLoading(false)
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    if (!form.name.trim())        return setError('Введите название')
    if (!form.amount)             return setError('Введите сумму')
    if (Number(form.amount) <= 0) return setError('Сумма должна быть больше 0')
    if (!form.date)               return setError('Выберите дату')

    const dto = {
      name:        form.name.trim(),
      categoryId:  form.categoryId ? Number(form.categoryId) : null,
      amount:      Number(form.amount),
      description: form.description.trim(),
      date:        form.date,
    }

    setLoading(true)
    try {
      if (isEdit) {
        await onUpdate(editItem.id, dto)
        setSuccess(true)
        setTimeout(() => { setSuccess(false); onClose() }, 1000)
      } else {
        await onSubmit(dto)
        setForm(f => ({ ...empty(), categoryId: f.categoryId, date: f.date }))
        setSuccess(true)
        setTimeout(() => setSuccess(false), 2000)
      }
      setError('')
    } catch (err) {
      setError(err?.response?.data?.message ?? 'Произошла ошибка')
    } finally {
      setLoading(false)
    }
  }

  const title = isEdit
    ? (isIncome ? 'Редактировать доход' : 'Редактировать расход')
    : (isIncome ? 'Добавить доход'      : 'Добавить расход')

  return (
    <Modal open={open} onClose={handleClose} title={title}>
      <form onSubmit={handleSubmit} className="space-y-4">
        {/* Name */}
        <div>
          <label className="label">Название *</label>
          <input
            className="input"
            placeholder={isIncome ? 'Например: Зарплата' : 'Например: Продукты'}
            maxLength={100}
            value={form.name}
            onChange={e => set('name', e.target.value)}
          />
        </div>

        {/* Amount + Category */}
        <div className="grid grid-cols-2 gap-3">
          <div>
            <div className="flex items-center mb-1">
              <label className="label !mb-0">Сумма, ₽ *</label>
            </div>
            <input
              className="input"
              type="number"
              min="0.01"
              step="0.01"
              placeholder="0"
              value={form.amount}
              onChange={e => set('amount', e.target.value)}
            />
          </div>
          <div>
            <div className="flex items-center justify-between mb-1">
              <label className="label !mb-0">Категория</label>
              <button
                type="button"
                onClick={() => { setShowNewCat(v => !v); setCatError('') }}
                className="text-xs text-secondary hover:text-primary flex items-center gap-1 transition-colors"
              >
                <i className={`fa-solid ${showNewCat ? 'fa-xmark' : 'fa-plus'} text-[10px]`} />
                {showNewCat ? 'Отмена' : 'Новая'}
              </button>
            </div>
            <CategorySelect
              categories={categories}
              value={form.categoryId}
              onChange={val => set('categoryId', val)}
            />
          </div>
        </div>

        {/* Inline new category form */}
        {showNewCat && (
          <div className="animate-fade-in bg-secondary/10 rounded-xl p-3 space-y-2">
            <p className="text-xs font-medium text-muted">Создать новую категорию</p>
            <div className="flex gap-2">
              <input
                className="input flex-1"
                placeholder="Название (макс. 20 символов)"
                maxLength={20}
                value={newCatName}
                onChange={e => { setNewCatName(e.target.value); setCatError('') }}
                onKeyDown={e => { if (e.key === 'Enter') { e.preventDefault(); handleCreateCategory() } }}
              />
              <button
                type="button"
                onClick={handleCreateCategory}
                disabled={catLoading}
                className="btn-primary flex-shrink-0"
              >
                {catLoading
                  ? <i className="fa-solid fa-spinner animate-spin" />
                  : <i className="fa-solid fa-check" />
                }
                Создать
              </button>
            </div>
            {catError && (
              <p className="text-xs text-expense flex items-center gap-1">
                <i className="fa-solid fa-circle-exclamation" /> {catError}
              </p>
            )}
          </div>
        )}

        {/* Date */}
        <div>
          <label className="label">Дата *</label>
          <input
            className="input"
            type="date"
            value={form.date}
            onChange={e => set('date', e.target.value)}
          />
        </div>

        {/* Description */}
        <div>
          <label className="label">Описание</label>
          <textarea
            className="input resize-none"
            rows={2}
            maxLength={255}
            placeholder="Дополнительные заметки..."
            value={form.description}
            onChange={e => set('description', e.target.value)}
          />
        </div>

        {error && (
          <p className="text-sm text-expense flex items-center gap-1.5">
            <i className="fa-solid fa-circle-exclamation" /> {error}
          </p>
        )}

        {success && (
          <p className="text-sm text-income flex items-center gap-1.5">
            <i className="fa-solid fa-circle-check" />
            {isEdit ? 'Изменения сохранены!' : (isIncome ? 'Доход добавлен!' : 'Расход добавлен!')}
          </p>
        )}

        <div className="flex items-center justify-between pt-1">
          <button
            type="button"
            onClick={() => setShowCalc(true)}
            className="w-10 h-10 flex items-center justify-center rounded-xl text-muted hover:text-primary hover:bg-secondary/20 transition-colors"
            title="Открыть калькулятор"
          >
            <i className="fa-solid fa-calculator text-xl" />
          </button>
          <div className="flex gap-3">
          <button type="button" className="btn-ghost" onClick={handleClose}>Закрыть</button>
          <button
            type="submit"
            disabled={loading}
            className={isIncome ? 'btn-income' : 'btn-expense'}
          >
            {loading && <i className="fa-solid fa-spinner animate-spin" />}
            {isEdit ? 'Сохранить' : (isIncome ? 'Добавить доход' : 'Добавить расход')}
          </button>
          </div>
        </div>
      </form>

      <CalculatorModal
        open={showCalc}
        onClose={() => setShowCalc(false)}
        onApply={(value) => set('amount', value)}
      />
    </Modal>
  )
}
