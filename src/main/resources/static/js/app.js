const API_BASE = 'http://localhost:8080/api/dashboard';
let currentChart = null;

// Инициализация при загрузке страницы
document.addEventListener('DOMContentLoaded', function() {
    loadAllData();
    setCurrentDate();
});

// Установка текущей даты по умолчанию
function setCurrentDate() {
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('date').value = today;
}

// Загрузка всех данных
async function loadAllData() {
    try {
        await Promise.all([
            loadTotals(),
            loadRecentTransactions(),
            loadChartData()
        ]);
    } catch (error) {
        showNotification('Ошибка загрузки данных', 'error');
        console.error('Ошибка загрузки данных:', error);
    }
}

// Загрузка общих сумм
async function loadTotals() {
    try {
        const response = await fetch(`${API_BASE}/totals`);
        if (!response.ok) throw new Error('Ошибка загрузки данных');

        const data = await response.json();

        const totalIncome = parseFloat(data.income) || 0;  // Преобразуем в число и устанавливаем 0 по умолчанию
        const totalExpense = parseFloat(data.expense) || 0;

        document.getElementById('total-income').textContent = formatCurrency(totalIncome);
        document.getElementById('total-expense').textContent = formatCurrency(totalExpense);
        document.getElementById('balance').textContent = formatCurrency(totalIncome - totalExpense);

        // Цвет баланса в зависимости от значения
        const balanceElement = document.getElementById('balance');
        const balance = data.Income - data.Expense;
        balanceElement.className = balance >= 0 ? 'positive' : 'negative';

    } catch (error) {
        console.error('Ошибка загрузки totals:', error);
        throw error;
    }
}

// Загрузка последних транзакций
async function loadRecentTransactions() {
    try {
        const [incomesResponse, expensesResponse] = await Promise.all([
            fetch(`${API_BASE}/incomes`),
            fetch(`${API_BASE}/expenses`)
        ]);

        const incomes = await incomesResponse.json();
        const expenses = await expensesResponse.json();

        displayRecentTransactions('recent-incomes', incomes.slice(-5), 'income');
        displayRecentTransactions('recent-expenses', expenses.slice(-5), 'expense');

    } catch (error) {
        console.error('Ошибка загрузки транзакций:', error);
        throw error;
    }
}

// Отображение последних транзакций
function displayRecentTransactions(elementId, transactions, type) {
    const container = document.getElementById(elementId);
    container.innerHTML = '';

    if (transactions.length === 0) {
        container.innerHTML = '<li class="no-data">Нет данных</li>';
        return;
    }

    transactions.reverse().forEach(transaction => {
        const li = document.createElement('li');
        li.className = `transaction-item ${type}`;
        li.innerHTML = `
            <span class="amount">${formatCurrency(transaction.amount)}</span>
            <span class="description">${transaction.description || 'Без описания'}</span>
            <span class="date">${formatDate(transaction.date)}</span>
        `;
        container.appendChild(li);
    });
}

// Загрузка данных для графика
async function loadChartData() {
    try {
        const [incomes, expenses] = await Promise.all([
            fetch(`${API_BASE}/incomes`).then(r => r.json()),
            fetch(`${API_BASE}/expenses`).then(r => r.json())
        ]);

        // Получаем текущий месяц
        const currentMonth = new Date().getMonth();
        const currentYear = new Date().getFullYear();

        // Фильтруем данные за текущий месяц
        const filteredIncomes = incomes.filter(income => {
            const incomeDate = new Date(income.date);
            return incomeDate.getMonth() === currentMonth && incomeDate.getFullYear() === currentYear;
        });

        const filteredExpenses = expenses.filter(expense => {
            const expenseDate = new Date(expense.date);
            return expenseDate.getMonth() === currentMonth && expenseDate.getFullYear() === currentYear;
        });


        createMainChart(filteredIncomes, filteredExpenses);
    } catch (error) {
        console.error('Ошибка загрузки данных для графика:', error);
        throw error;
    }
}

// Создание основного графика
function createMainChart(incomes, expenses) {
    const ctx = document.getElementById('financeChart').getContext('2d');

    if (currentChart) {
        currentChart.destroy();
    }

    const totalIncome = incomes.reduce((sum, item) => sum + parseFloat(item.amount), 0);
    const totalExpense = expenses.reduce((sum, item) => sum + parseFloat(item.amount), 0);

    currentChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: ['Доходы', 'Расходы', 'Баланс'],
            datasets: [{
                label: 'Сумма (₽)',
                data: [totalIncome, totalExpense, totalIncome - totalExpense],
                backgroundColor: [
                    'rgba(75, 192, 192, 0.7)',
                    'rgba(255, 99, 132, 0.7)',
                    'rgba(54, 162, 235, 0.7)'
                ],
                borderColor: [
                    'rgba(75, 192, 192, 1)',
                    'rgba(255, 99, 132, 1)',
                    'rgba(54, 162, 235, 1)'
                ],
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            return `Сумма: ${formatCurrency(context.raw)}`;
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: function(value) {
                            return formatCurrency(value);
                        }
                    }
                }
            }
        }
    });
}

// Показать модальное окно
function showAddForm(type) {
    const modal = document.getElementById('modal');
    const title = document.getElementById('modal-title');
    const typeSelect = document.getElementById('type');

    typeSelect.value = type;
    title.textContent = `Добавить ${type === 'income' ? 'доход' : 'расход'}`;
    modal.style.display = 'block';

    // Очистка формы
    document.getElementById('finance-form').reset();
    setCurrentDate();
}

// Скрыть модальное окно
function hideModal() {
    document.getElementById('modal').style.display = 'none';
}

// Отправка формы
async function submitForm(event) {
    event.preventDefault();

    const formData = {
        category: document.getElementById('category').value,
        amount: parseFloat(document.getElementById('amount').value),
        description: document.getElementById('description').value,
        date: document.getElementById('date').value
    };

    // Валидация
    if (!validateForm(formData)) {
        return;
    }

    const type = document.getElementById('type').value;
    const endpoint = type === 'income' ? '/addIncome' : '/addExpense';

    try {
        const response = await fetch(`${API_BASE}${endpoint}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });

        if (response.ok) {
            showNotification('Данные успешно добавлены!', 'success');
            hideModal();
            await loadAllData(); // Перезагрузка всех данных
        } else {
            const errorText = await response.text();
            throw new Error(errorText);
        }
    } catch (error) {
        console.error('Ошибка добавления данных:', error);
        showNotification('Ошибка при добавлении данных', 'error');
    }
}

// Валидация формы
function validateForm(data) {
    if (data.amount <= 0) {
        showNotification('Сумма должна быть положительным числом', 'error');
        return false;
    }

    if (data.description && data.description.length > 255) {
        showNotification('Описание не может быть длиннее 255 символов', 'error');
        return false;
    }

    if (data.category && data.category.length > 100) {
        showNotification('Категория не может быть длиннее 100 символов', 'error');
        return false;
    }

    if (!data.date) {
        showNotification('Дата обязательна для заполнения', 'error');
        return false;
    }

    return true;
}

// Удаление всех данных
async function deleteAllData() {
    if (!confirm('Вы уверены, что хотите удалить ВСЕ данные? Это действие нельзя отменить.')) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/delete/all`, {
            method: 'DELETE'
        });

        if (response.ok) {
            showNotification('Все данные успешно удалены', 'success');
            await loadAllData(); // Обновление данных
        } else {
            throw new Error('Ошибка удаления');
        }
    } catch (error) {
        console.error('Ошибка удаления:', error);
        showNotification('Ошибка при удалении данных', 'error');
    }
}

// Переход к детальной статистике
function loadDetailedView() {
    window.location.href = 'dashboard.html';
}


// Показать уведомление
function showNotification(message, type = 'info') {
    const notification = document.getElementById('notification');
    notification.textContent = message;
    notification.className = `notification ${type}`;
    notification.style.display = 'block';

    setTimeout(() => {
        notification.style.display = 'none';
    }, 3000);
}

// Форматирование валюты
function formatCurrency(amount) {
    return new Intl.NumberFormat('ru-RU', {
        style: 'currency',
        currency: 'RUB',
        minimumFractionDigits: 2
    }).format(amount);
}

// Форматирование даты
function formatDate(dateString) {
    return new Date(dateString).toLocaleDateString('ru-RU');
}

// Закрытие модального окна по клику вне его
window.onclick = function(event) {
    const modal = document.getElementById('modal');
    if (event.target === modal) {
        hideModal();
    }
}