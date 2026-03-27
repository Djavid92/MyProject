import axios from 'axios'

const api = axios.create({ baseURL: '/api/dashboard' })

// ── Totals ─────────────────────────────────────────────────────────────────
export const getTotals = () =>
  api.get('/totals').then(r => r.data)

export const getTotalsByMonth = (year, month) =>
  api.get('/totals/by-month', { params: { year, month } }).then(r => r.data)

// ── Operations ─────────────────────────────────────────────────────────────
export const getOperationsByMonth = (year, month) =>
  api.get('/operations/by-month', { params: { year, month } }).then(r => r.data)

// ── Income ─────────────────────────────────────────────────────────────────
export const addIncome = (dto) =>
  api.post('/addIncome', dto).then(r => r.data)

export const updateIncome = (id, dto) =>
  api.put(`/incomes/${id}`, dto)

export const deleteIncome = (id) =>
  api.delete(`/incomes/${id}`)

// ── Expense ────────────────────────────────────────────────────────────────
export const addExpense = (dto) =>
  api.post('/addExpense', dto).then(r => r.data)

export const updateExpense = (id, dto) =>
  api.put(`/expenses/${id}`, dto)

export const deleteExpense = (id) =>
  api.delete(`/expenses/${id}`)

// ── Categories ─────────────────────────────────────────────────────────────
export const getCategories = () =>
  api.get('/categories').then(r => r.data)

export const addCategory = (dto) =>
  api.post('/addCategory', dto).then(r => r.data)

export const deleteCategory = (id) =>
  api.delete(`/category/${id}`)

