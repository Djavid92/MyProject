CREATE TABLE IF NOT EXISTS category (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS income (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255),
    category_id BIGINT NOT NULL,
    amount      NUMERIC(38, 2),
    description VARCHAR(255),
    date        DATE,
    CONSTRAINT fk_income_category FOREIGN KEY (category_id) REFERENCES category(id)
);

CREATE TABLE IF NOT EXISTS expense (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255),
    category_id BIGINT NOT NULL,
    amount      NUMERIC(38, 2),
    description VARCHAR(255),
    date        DATE,
    CONSTRAINT fk_expense_category FOREIGN KEY (category_id) REFERENCES category(id)
);
