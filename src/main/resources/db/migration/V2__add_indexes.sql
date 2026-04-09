-- Индексы для фильтрации по дате (запросы по диапазону месяца)
CREATE INDEX IF NOT EXISTS idx_income_date  ON income(date);
CREATE INDEX IF NOT EXISTS idx_expense_date ON expense(date);

-- Индексы по внешнему ключу category_id (JOIN с таблицей category)
CREATE INDEX IF NOT EXISTS idx_income_category_id  ON income(category_id);
CREATE INDEX IF NOT EXISTS idx_expense_category_id ON expense(category_id);
