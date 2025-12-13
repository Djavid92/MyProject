// script.js

const API_BASE_URL = '/api/dashboard';

// ====================================================================
// ОБЩИЕ ФУНКЦИИ
// ====================================================================

/**
 * Выполняет GET запрос к API с возможными параметрами startDate и endDate.
 * @param {string} endpoint - Конечная точка API, например '/totals'
 * @param {string} [startDate] - Начальная дата в формате 'yyyy-MM-dd'.
 * @param {string} [endDate] - Конечная дата в формате 'yyyy-MM-dd'.
 * @returns {Promise<Object>} Данные JSON.
 */
async function fetchApiData(endpoint, startDate = null, endDate = null) {
    try {
        let url = `${API_BASE_URL}${endpoint}`;

        // Добавляем параметры запроса, если они присутствуют
        const params = new URLSearchParams();
        if (startDate) params.append('startDate', startDate);
        if (endDate) params.append('endDate', endDate);

        if (params.toString()) {
            url += `?${params.toString()}`;
        }

        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`Ошибка сети: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error(`Ошибка при получении данных с ${endpoint}:`, error);
        return null;
    }
}

/**
 * Отправляет POST запрос к API.
 * @param {string} endpoint - Конечная точка API, например '/addIncome'
 * @param {Object} data - Объект данных для отправки.
 * @returns {Promise<boolean>} Успешность операции.
 */
async function postApiData(endpoint, data) {
    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        });

        if (response.status === 201) {
            alert('Операция успешно добавлена!');
            return true;
        } else {
            // Для отображения ошибок валидации из Spring
            const errorText = await response.text();
            alert(`Ошибка при добавлении: ${errorText}`);
            return false;
        }
    } catch (error) {
        console.error(`Ошибка при отправке данных на ${endpoint}:`, error);
        alert('Произошла ошибка сети/сервера.');
        return false;
    }
}

/**
 * Отправляет DELETE запрос к API.
 * @param {string} endpoint - Конечная точка API, например '/incomes/1'
 * @returns {Promise<boolean>} Успешность операции.
 */
async function deleteApiData(endpoint) {
    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'DELETE',
        });

        if (response.status === 204 || response.status === 200) {
            alert('Операция успешно удалена!');
            return true;
        } else if (response.status === 404) {
            alert('Ошибка: Операция не найдена.');
            return false;
        } else {
            alert(`Ошибка при удалении: ${response.statusText}`);
            return false;
        }
    } catch (error) {
        console.error(`Ошибка при удалении данных с ${endpoint}:`, error);
        alert('Произошла ошибка сети/сервера при удалении.');
        return false;
    }
}

// ====================================================================
// ГЛАВНАЯ СТРАНИЦА (index.html)
// ====================================================================

/**
 * Загружает и отображает общие суммы доходов и расходов за текущий месяц.
 */
async function loadCurrentTotals() {
    const totals = await fetchApiData('/totals');
    if (totals) {
        const income = totals.income || 0;
        const expense = totals.expense || 0;
        const balance = income - expense;

        document.getElementById('totalIncome').textContent = `${income.toFixed(2)} ₽`;
        document.getElementById('totalExpense').textContent = `${expense.toFixed(2)} ₽`;
        document.getElementById('currentBalance').textContent = `${balance.toFixed(2)} ₽`;

        // Обновление стиля баланса
        const balanceElement = document.getElementById('currentBalance');
        balanceElement.classList.remove('income-text', 'expense-text');
        if (balance > 0) {
            balanceElement.classList.add('income-text');
        } else if (balance < 0) {
            balanceElement.classList.add('expense-text');
        }
    }
}

// ====================================================================
// ПАНЕЛЬ УПРАВЛЕНИЯ (dashboard.html)
// ====================================================================

/**
 * Загружает и отображает список доходов.
 */
async function loadIncomes(startDate = null, endDate = null) {
    const incomes = await fetchApiData('/incomes', startDate, endDate);
    const tableBody = document.getElementById('incomesTableBody');
    tableBody.innerHTML = '';

    if (incomes && incomes.length > 0) {
        incomes.forEach(item => {
            tableBody.innerHTML += `
                <tr>
                    <td>${item.category}</td>
                    <td><span class="income-text">${item.amount.toFixed(2)} ₽</span></td>
                    <td>${new Date(item.date).toLocaleDateString()}</td>
                    <td>
                        <button class="btn btn-danger btn-sm btn-delete" onclick="deleteOperation('incomes', ${item.id})">Удалить</button>
                    </td>
                </tr>
            `;
        });
    } else {
        tableBody.innerHTML = '<tr><td colspan="4">Нет записей о доходах.</td></tr>';
    }
}

/**
 * Загружает и отображает список расходов.
 */
async function loadExpenses(startDate = null, endDate = null) {
    const expenses = await fetchApiData('/expenses', startDate, endDate);
    const tableBody = document.getElementById('expensesTableBody');
    tableBody.innerHTML = '';

    if (expenses && expenses.length > 0) {
        expenses.forEach(item => {
            tableBody.innerHTML += `
                <tr>
                    <td>${item.category}</td>
                    <td><span class="expense-text">${item.amount.toFixed(2)} ₽</span></td>
                    <td>${new Date(item.date).toLocaleDateString()}</td>
                    <td>
                        <button class="btn btn-danger btn-sm btn-delete" onclick="deleteOperation('expenses', ${item.id})">Удалить</button>
                    </td>
                </tr>
            `;
        });
    } else {
        tableBody.innerHTML = '<tr><td colspan="4">Нет записей о расходах.</td></tr>';
    }
}

/**
 * Обработчик для применения фильтра.
 */
async function filterData() {
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;

    await loadIncomes(startDate, endDate);
    await loadExpenses(startDate, endDate);
}

/**
 * Обрабатывает отправку формы добавления дохода.
 */
async function handleAddIncome(event) {
    event.preventDefault();
    const form = event.target;
    console.log('Форма отправлена:', form);
    const incomeData = {
        category: form.category.value,
        amount: parseFloat(form.amount.value),
        date: form.date.value
    };

    if (await postApiData('/addIncome', incomeData)) {
        form.reset();
        await loadIncomes(); // Обновить список
    }
}

/**
 * Обрабатывает отправку формы добавления расхода.
 */
async function handleAddExpense(event) {
    event.preventDefault();
    const form = event.target;
    const expenseData = {
        category: form.category.value,
        amount: parseFloat(form.amount.value),
        date: form.date.value
    };

    if (await postApiData('/addExpense', expenseData)) {
        form.reset();
        await loadExpenses(); // Обновить список
    }
}

/**
 * Удаляет операцию по ID.
 * @param {string} type - 'incomes' или 'expenses'
 * @param {number} id - ID операции
 */
async function deleteOperation(type, id) {
    if (confirm(`Вы уверены, что хотите удалить ${type.slice(0, -1)} с ID ${id}?`)) {
        if (await deleteApiData(`/${type}/${id}`)) {
            if (type === 'incomes') {
                await loadIncomes();
            } else {
                await loadExpenses();
            }
        }
    }
}

/**
 * Удаляет все доходы и расходы.
 */
async function deleteAllOperations() {
    if (confirm("ВНИМАНИЕ! Вы уверены, что хотите безвозвратно удалить ВСЕ доходы и расходы?")) {
        if (await deleteApiData('/delete/all')) {
            // Обновить обе таблицы после удаления
            if (document.getElementById('incomesTableBody')) {
                await loadIncomes();
            }
            if (document.getElementById('expensesTableBody')) {
                await loadExpenses();
            }
            if (document.getElementById('totalIncome')) {
                await loadCurrentTotals();
            }
        }
    }
}


// ====================================================================
// ПРЕДЫДУЩИЙ МЕСЯЦ (previousInfo.html)
// ====================================================================

/**
 * Загружает и отображает общие суммы доходов и расходов за предыдущий месяц.
 */
async function loadPreviousTotals() {
    const totals = await fetchApiData('/balance');
    if (totals) {
        const income = totals.previousIncome || 0;
        const expense = totals.previousExpense || 0;
        const balance = income - expense;

        document.getElementById('prevTotalIncome').textContent = `${income.toFixed(2)} ₽`;
        document.getElementById('prevTotalExpense').textContent = `${expense.toFixed(2)} ₽`;
        document.getElementById('prevBalance').textContent = `${balance.toFixed(2)} ₽`;

        // Обновление стиля баланса
        const balanceElement = document.getElementById('prevBalance');
        balanceElement.classList.remove('income-text', 'expense-text');
        if (balance > 0) {
            balanceElement.classList.add('income-text');
        } else if (balance < 0) {
            balanceElement.classList.add('expense-text');
        }
    }
}


// ====================================================================
// ЗАПУСК ПРИ ЗАГРУЗКЕ СТРАНИЦЫ
// ====================================================================

// В script.js, в самом конце:

// script.js (НОВЫЙ КОД ЗАПУСКА В КОНЦЕ ФАЙЛА)

document.addEventListener('DOMContentLoaded', () => {
    const currentPath = window.location.pathname;

    // --- 1. Навигация (подсветка активной ссылки) ---
    document.querySelectorAll('.header-nav a').forEach(link => {
        // Проверяем, соответствует ли href ссылки текущему пути или его началу
        if (currentPath === link.getAttribute('href') || (currentPath === '/' && link.getAttribute('href') === '/')) {
            link.classList.add('active');
        }
    });

    // --- 2. Логика для index.html и dashboard.html (Текущий месяц) ---
    if (currentPath === '/' || currentPath === '/dashboard') {
        console.log("-> Запуск загрузки общих сумм (Текущий месяц) на", currentPath);
        loadCurrentTotals();
    }

    // --- 3. Логика, специфичная только для Дашборда (/dashboard) ---
    if (currentPath === '/dashboard') {
        console.log("-> Запуск загрузки списков операций");
        loadIncomes();
        loadExpenses();

        // Назначить обработчики для форм
        const incomeForm = document.getElementById('addIncomeForm');
        if (incomeForm) incomeForm.addEventListener('submit', handleAddIncome);

        const expenseForm = document.getElementById('addExpenseForm');
        if (expenseForm) expenseForm.addEventListener('submit', handleAddExpense);

        const deleteAllBtn = document.getElementById('deleteAllBtn');
        if (deleteAllBtn) deleteAllBtn.addEventListener('click', deleteAllOperations);
    }

    // --- 4. Логика, специфичная для предыдущего месяца (/previous) ---
    if (currentPath === '/previous') {
        console.log("-> Запуск загрузки общих сумм (Предыдущий месяц)");
        loadPreviousTotals();
    }
});