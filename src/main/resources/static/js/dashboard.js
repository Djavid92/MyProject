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
function createCategoryChart(canvasId, data, title) {
    const ctx = document.getElementById(canvasId).getContext('2d');

    // Группировка по категориям
    const categories = {};
    data.forEach(item => {
        const category = item.category || 'Без категории';
        categories[category] = (categories[category] || 0) + parseFloat(item.amount);
    });

    new Chart(ctx, {
        type: 'pie',
        data: {
            labels: Object.keys(categories),
            datasets: [{
                data: Object.values(categories),
                backgroundColor: [
                    '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0',
                    '#9966FF', '#FF9F40', '#FF6384', '#C9CBCF'
                ]
            }]
        },
        options: {
            responsive: true,
            plugins: {
                title: {
                    display: true,
                    text: title
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const value = context.raw;
                            const total = context.dataset.data.reduce((a, b) => a + b, 0);
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

// Функции удаления (заглушки - нужно реализовать на бэкенде)
function deleteIncome(id) {
    if (confirm('Удалить этот доход?')) {
        alert('Функция удаления будет реализована в следующей версии');
    }
}

function deleteExpense(id) {
    if (confirm('Удалить этот расход?')) {
        alert('Функция удаления будет реализована в следующей версии');
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