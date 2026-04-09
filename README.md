# Income & Expense Tracker

Веб-приложение для учёта личных финансов — доходов и расходов, с визуализацией данных, категориями и мониторингом.

## Стек технологий

**Backend**
- Java 17, Spring Boot 3.5.5
- PostgreSQL + Spring Data JPA + Flyway
- Redis (кэширование)
- Spring AOP, MapStruct, Lombok
- Swagger / OpenAPI (springdoc 2.7.0)

**Frontend**
- React 18, Vite, Tailwind CSS
- Chart.js (круговые диаграммы)
- Axios, React Router 6

**Мониторинг**
- Prometheus + Grafana
- Spring Boot Actuator + Micrometer

**Тестирование**
- JUnit 5, Mockito
- Testcontainers (интеграционные тесты)
- JaCoCo (минимум 80% покрытия)

---

## Быстрый старт

### Вариант 1 — Docker (рекомендуется)

```bash
# Собрать JAR
./mvnw clean package -DskipTests

# Запустить всё окружение (PostgreSQL + Redis + приложение)
docker-compose -f docker-compose.prod.yaml up --build
```

Приложение будет доступно на: **http://localhost:8082**

---

### Вариант 2 — Локальная разработка

**Шаг 1.** Запустить PostgreSQL и Redis через Docker:

```bash
docker-compose -f docker-compose.dev.yaml up
```

**Шаг 2.** Запустить backend:

```bash
./mvnw spring-boot:run
```

**Шаг 3.** Запустить frontend:

```bash
cd frontend
npm install
npm run dev
```

| Сервис       | URL                                      |
|--------------|------------------------------------------|
| Frontend     | http://localhost:3000                    |
| Backend API  | http://localhost:8081/api                |
| Swagger UI   | http://localhost:8081/swagger-ui.html    |
| Actuator     | http://localhost:8081/actuator           |

---

### Вариант 3 — Мониторинг

```bash
docker-compose -f docker-compose.prometheus.yaml up
```

| Сервис     | URL                        | Доступ        |
|------------|----------------------------|---------------|
| Grafana    | http://localhost:3000      | admin / admin |
| Prometheus | http://localhost:9090      | —             |

---

## API

Base path: `/api/dashboard`

| Метод  | Путь                          | Описание                          |
|--------|-------------------------------|-----------------------------------|
| GET    | `/totals`                     | Итоги за текущий месяц            |
| GET    | `/totals/by-month`            | Итоги за указанный месяц/год      |
| GET    | `/operations/by-month`        | Операции за указанный месяц       |
| GET    | `/operations/by-categoryIncome`  | Доходы по категории            |
| GET    | `/operations/by-categoryExpense` | Расходы по категории           |
| POST   | `/addIncome`                  | Добавить доход                    |
| POST   | `/addExpense`                 | Добавить расход                   |
| PUT    | `/incomes/{id}`               | Обновить доход                    |
| PUT    | `/expenses/{id}`              | Обновить расход                   |
| DELETE | `/incomes/{id}`               | Удалить доход                     |
| DELETE | `/expenses/{id}`              | Удалить расход                    |
| POST   | `/addCategory`                | Добавить категорию                |
| GET    | `/categories`                 | Список всех категорий             |
| DELETE | `/category/{id}`              | Удалить категорию                 |
| GET    | `/calculate`                  | Калькулятор                       |

Интерактивная документация: **http://localhost:8081/swagger-ui.html**

---

## Схема базы данных

```
category (id, name)
income   (id, name, category_id, amount, description, date)
expense  (id, name, category_id, amount, description, date)
```

Миграции управляются через **Flyway** (папка `src/main/resources/db/migration`).

---

## Тесты

```bash
# Запустить все тесты
./mvnw test

# Запустить только интеграционные тесты (требуется Docker для Testcontainers)
./mvnw test -Dtest=FinanceIntegrationTest
```

---

## Структура проекта

```
demo/
├── src/main/java/         # Backend (Spring Boot)
├── src/main/resources/    # Конфиги, миграции Flyway, статика
├── src/test/              # Unit и интеграционные тесты
├── frontend/              # React-приложение (Vite)
├── config/                # prometheus.yml, Grafana config
├── Dockerfile
├── docker-compose.dev.yaml
├── docker-compose.prod.yaml
└── docker-compose.prometheus.yaml
```
