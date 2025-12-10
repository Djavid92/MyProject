// script.js

document.addEventListener("DOMContentLoaded", function() {
    fetch('/api/dashboard/balance') // Запрос к вашему API
        .then(response => response.json())
        .then(data => {
            const previousIncome = parseFloat(data.previousIncome) || 0;  // Преобразуем в число и устанавливаем 0 по умолчанию
            const previousExpense = parseFloat(data.previousExpense) || 0;
            // Обновляем текст на странице с полученными данными
            document.getElementById("previousIncome").textContent = "Доходы: " + formatCurrency(previousIncome);
            document.getElementById("previousExpense").textContent = "Расходы: " + formatCurrency(previousExpense);
            document.getElementById("balance").textContent = "Остаток: " + formatCurrency(previousIncome - previousExpense);
        })
        .catch(error => {
            console.error("Ошибка при загрузке данных:", error);
            document.getElementById("previousIncome").textContent = "Ошибка загрузки данных.";
            document.getElementById("previousExpense").textContent = "Ошибка загрузки данных.";
            document.getElementById("balance").textContent = "Ошибка загрузки данных.";
        });
});

// Функция для форматирования чисел в валюту
function formatCurrency(value) {
    return new Intl.NumberFormat('ru-RU', {
        style: 'currency',
        currency: 'RUB'
    }).format(value);
}