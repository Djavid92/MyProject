import { useState, useEffect } from 'react'
import Modal from '../ui/Modal.jsx'

const today = () => new Date().toISOString().split('T')[0]

const empty = () => ({ name: '', categoryId: '', amount: '', description: '', date: today() })

export default function AddTransactionModal({ type, open, onClose, categories, onSubmit, onAddCategory, editItem, onUpdate }) {
  const isIncome  = type === 'income'
  const isEdit    = !!editItem
  const [form, setForm]               = useState(empty())
  const [loading, setLoading]         = useState(false)
  const [error, setError]             = useState('')
  const [success, setSuccess]         = useState(false)

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
  const [showNewCat, setShowNewCat]   = useState(false)
  const [newCatName, setNewCatName]   = useState('')
  const [catLoading, setCatLoading]   = useState(false)
  const [catError, setCatError]       = useState('')

  const set = (k, v) => setForm(f => ({ ...f, [k]: v }))

  const reset = () => { setForm(empty()); setError(''); setSuccess(false); setShowNewCat(false); setNewCatName(''); setCatError('') }

  const handleClose = () => { reset(); onClose() }

  const handleCreateCategory = async () => {
    const trimmed = newCatName.trim()
    if (!trimmed) return setCatError('Введите название')
    if (trimmed.length > 20) return setCatError('Максимум 20 символов')
    setCatError('')
    setCatLoading(true)
    try {
      const created = await onAddCategory({ name: trimmed })
      // auto-select the newly created category
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
            <label className="label">Сумма, ₽ *</label>
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
            <div className="relative">
              <select
                value={form.categoryId}
                onChange={e => set('categoryId', e.target.value)}
                className="input appearance-none pr-7"
              >
                <option value="">— без категории —</option>
                {categories.map(c => (
                  <option key={c.id} value={c.id}>{c.name}</option>
                ))}
              </select>
              <i className="fa-solid fa-chevron-down absolute right-2.5 top-1/2 -translate-y-1/2 text-muted text-xs pointer-events-none" />
            </div>
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

        <div className="flex justify-end gap-3 pt-1">
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
      </form>
    </Modal>
  )
}
