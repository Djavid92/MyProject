const API_BASE = 'http://localhost:8080/api/dashboard';
let incomes = [];
let expenses = [];

// Инициализация dashboard
document.addEventListener('DOMContentLoaded', function() {
    loadDashboardData();
    setFilterDates();
});

// Установка дат фильтра по умолчанию
function setFilterDates() {
    const now = new Date();
    const firstDay = new Date(now.getFullYear(), now.getMonth(), 1);

    document.getElementById('date-from').value = firstDay.toISOString().split('T')[0];
    document.getElementById('date-to').value = now.toISOString().split('T')[0];
}

// Загрузка данных для dashboard
async function loadDashboardData() {
    try {
        [incomes, expenses] = await Promise.all([
            fetch(`${API_BASE}/incomes`).then(r => r.json()),
            fetch(`${API_BASE}/expenses`).then(r => r.json())
        ]);

        applyFilters();
    } catch (error) {
        console.error('Ошибка загрузки данных:', error);
        alert('Ошибка загрузки данных');
    }
}

// Применение фильтров
function applyFilters() {
    const dateFrom = document.getElementById('date-from').value;
    const dateTo = document.getElementById('date-to').value;

    let filteredIncomes = incomes;
    let filteredExpenses = expenses;

    if (dateFrom) {
        filteredIncomes = filteredIncomes.filter(income => income.date >= dateFrom);
        filteredExpenses = filteredExpenses.filter(expense => expense.date >= dateFrom);
    }

    if (dateTo) {
        filteredIncomes = filteredIncomes.filter(income => income.date <= dateTo);
        filteredExpenses = filteredExpenses.filter(expense => expense.date <= dateTo);
    }

    displayIncomesTable(filteredIncomes);
    displayExpensesTable(filteredExpenses);
    updateCharts(filteredIncomes, filteredExpenses);
}

// Сброс фильтров
function resetFilters() {
    document.getElementById('date-from').value = '';
    document.getElementById('date-to').value = '';
    setFilterDates();
    applyFilters();
}

// переход на детальную информацию прошлого месяца
function loadPreviousDetailedView() {
    window.location.href = 'previousInfo.html';
}

// Отображение таблицы доходов
function displayIncomesTable(incomesData) {
    const tbody = document.querySelector('#incomes-table tbody');
    tbody.innerHTML = '';

    if (incomesData.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="no-data">Нет данных</td></tr>';
        return;
    }

    incomesData.sort((a, b) => new Date(b.date) - new Date(a.date)).forEach(income => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${formatDate(income.date)}</td>
            <td>${income.category || 'Не указана'}</td>
            <td class="amount positive">${formatCurrency(income.amount)}</td>
            <td>${income.description || '-'}</td>
            <td>
                <button class="btn btn-sm btn-danger" onclick="deleteIncome(${income.id})">Удалить</button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

// Отображение таблицы расходов
function displayExpensesTable(expensesData) {
    const tbody = document.querySelector('#expenses-table tbody');
    tbody.innerHTML = '';

    if (expensesData.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="no-data">Нет данных</td></tr>';
        return;
    }

    expensesData.sort((a, b) => new Date(b.date) - new Date(a.date)).forEach(expense => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${formatDate(expense.date)}</td>
            <td>${expense.category || 'Не указана'}</td>
            <td class="amount negative">${formatCurrency(expense.amount)}</td>
            <td>${expense.description || '-'}</td>
            <td>
                <button class="btn btn-sm btn-danger" onclick="deleteExpense(${expense.id})">Удалить</button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

// Обновление графиков
function updateCharts(incomesData, expensesData) {
    createCategoryChart('income-categories-chart', incomesData, 'Доходы по категориям');
    createCategoryChart('expense-categories-chart', expensesData, 'Расходы по категориям');
}

// Создание графика по категориям
// Хранилище инстансов графиков по id canvas
const charts = {};

// Создание/пересоздание графика по категориям
function createCategoryChart(canvasId, data, title) {
    const canvas = document.getElementById(canvasId);
    if (!canvas) return;

    // если график уже был — уничтожаем, чтобы освободить canvas
    if (charts[canvasId]) {
        charts[canvasId].destroy();
        charts[canvasId] = null;
    }

    // Группировка по категориям
    const categories = {};
    data.forEach(item => {
        const category = item.category || 'Без категории';
        categories[category] = (categories[category] || 0) + parseFloat(item.amount);
    });

    const labels = Object.keys(categories);
    const values = Object.values(categories);

    // Если данных нет — просто выходим (canvas останется пустым)
    if (values.length === 0) return;

    charts[canvasId] = new Chart(canvas, {
        type: 'pie',
        data: {
            labels,
            datasets: [{
                data: values,
                backgroundColor: [
                    '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0',
                    '#9966FF', '#FF9F40', '#C9CBCF', '#8BC34A',
                    '#00BCD4', '#FFC107', '#9C27B0', '#607D8B'
                ]
            }]
        },
        options: {
            responsive: true,
            plugins: {
                title: { display: true, text: title },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const value = Number(context.raw) || 0;
                            const total = context.dataset.data.reduce((a, b) => Number(a) + Number(b), 0) || 1;
                            const percentage = ((value / total) * 100).toFixed(1);
                            return `${context.label}: ${formatCurrency(value)} (${percentage}%)`;
                        }
                    }
                }
            }
        }
    });
}

// Управление вкладками
function openTab(tabName) {
    // Скрыть все вкладки
    document.querySelectorAll('.tab-content').forEach(tab => {
        tab.classList.remove('active');
    });

    // Убрать активный класс у всех кнопок
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.remove('active');
    });

    // Показать выбранную вкладку
    document.getElementById(tabName).classList.add('active');

    // Активировать кнопку
    event.currentTarget.classList.add('active');
}

// --- Удаление дохода ---
async function deleteIncome(id) {
    if (!confirm('Удалить этот доход?')) return;

    // если нужен CSRF-токен, положи его в <meta name="csrf-token" content="...">
    const csrf = document.querySelector('meta[name="csrf-token"]')?.getAttribute('content');

    // найдём кнопку и временно заблокируем
    const btn = document.querySelector(`#incomes-table button[onclick="deleteIncome(${id})"]`);
    if (btn) { btn.disabled = true; btn.textContent = 'Удаление...'; }

    try {
        const res = await fetch(`${API_BASE}/incomes/${id}`, {
            method: 'DELETE',
            headers: {
                'Accept': 'application/json',
                ...(csrf ? { 'X-CSRF-TOKEN': csrf } : {})
            }
        });

        if (!res.ok) {
            // попробуем достать текст ошибки с бэка
            let msg = 'Не удалось удалить доход';
            try { msg = (await res.json())?.message || msg; } catch (_) {}
            throw new Error(msg);
        }

        // локально удаляем запись и обновляем UI
        incomes = incomes.filter(i => i.id !== id);
        applyFilters();
    } catch (err) {
        console.error(err);
        alert(err.message || 'Ошибка при удалении дохода');
        if (btn) { btn.disabled = false; btn.textContent = 'Удалить'; }
    }
}

// --- Удаление расхода ---
async function deleteExpense(id) {
    if (!confirm('Удалить этот расход?')) return;

    const csrf = document.querySelector('meta[name="csrf-token"]')?.getAttribute('content');
    const btn = document.querySelector(`#expenses-table button[onclick="deleteExpense(${id})"]`);
    if (btn) { btn.disabled = true; btn.textContent = 'Удаление...'; }

    try {
        const res = await fetch(`${API_BASE}/expenses/${id}`, {
            method: 'DELETE',
            headers: {
                'Accept': 'application/json',
                ...(csrf ? { 'X-CSRF-TOKEN': csrf } : {})
            }
        });

        if (!res.ok) {
            let msg = 'Не удалось удалить расход';
            try { msg = (await res.json())?.message || msg; } catch (_) {}
            throw new Error(msg);
        }

        expenses = expenses.filter(e => e.id !== id);
        applyFilters();
    } catch (err) {
        console.error(err);
        alert(err.message || 'Ошибка при удалении расхода');
        if (btn) { btn.disabled = false; btn.textContent = 'Удалить'; }
    }
}

// Вспомогательные функции
function formatCurrency(amount) {
    return new Intl.NumberFormat('ru-RU', {
        style: 'currency',
        currency: 'RUB',
        minimumFractionDigits: 2
    }).format(amount);
}

function formatDate(dateString) {
    return new Date(dateString).toLocaleDateString('ru-RU');
}