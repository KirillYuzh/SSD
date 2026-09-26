-- Схема базы данных SQLite.
--
-- Типы столбцов в SQLite задают аффинность, а не строгий тип: аффинность NUMERIC
-- сохраняет целые числа как числа, поэтому значения дат, которые JDBC-драйвер
-- пишет числами, читаются без потери. Схема создаётся при каждом запуске,
-- поэтому используется CREATE TABLE IF NOT EXISTS: данные сохраняются между
-- перезапусками приложения.

CREATE TABLE IF NOT EXISTS users
(
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    username  VARCHAR(32)  NOT NULL UNIQUE,
    full_name VARCHAR(128) NOT NULL,
    email     VARCHAR(254) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS animals
(
    id      INTEGER PRIMARY KEY AUTOINCREMENT,
    name    VARCHAR(64) NOT NULL,
    species VARCHAR(64) NOT NULL,
    age     INTEGER     NOT NULL CHECK (age >= 0),
    status  VARCHAR(32) NOT NULL
);

CREATE TABLE IF NOT EXISTS enclosures
(
    id       INTEGER PRIMARY KEY AUTOINCREMENT,
    name     VARCHAR(64) NOT NULL,
    capacity INTEGER     NOT NULL CHECK (capacity > 0)
);

-- Допустимые виды животных хранятся в отдельной таблице, одна строка на вид.
CREATE TABLE IF NOT EXISTS enclosure_species
(
    enclosure_id INTEGER    NOT NULL REFERENCES enclosures (id) ON DELETE CASCADE,
    species      VARCHAR(64) NOT NULL,
    PRIMARY KEY (enclosure_id, species)
);

CREATE TABLE IF NOT EXISTS adoption_applications
(
    id           INTEGER     PRIMARY KEY AUTOINCREMENT,
    animal_id    INTEGER     NOT NULL REFERENCES animals (id),
    applicant_id INTEGER     NOT NULL REFERENCES users (id),
    status       VARCHAR(32) NOT NULL,
    submitted_at NUMERIC     NOT NULL
);

CREATE TABLE IF NOT EXISTS animal_handover_records
(
    id                INTEGER PRIMARY KEY AUTOINCREMENT,
    application_id    INTEGER     NOT NULL REFERENCES adoption_applications (id),
    preparation_date  NUMERIC     NOT NULL,
    confirmation_date NUMERIC,
    status            VARCHAR(32) NOT NULL
);

-- Ссылки на животных, заявителей и заявки защищены внешними ключами: удаление
-- строки, на которую кто-то ссылается, невозможно на уровне базы данных.
CREATE INDEX IF NOT EXISTS idx_adoption_applications_animal ON adoption_applications (animal_id);
CREATE INDEX IF NOT EXISTS idx_adoption_applications_applicant ON adoption_applications (applicant_id);
CREATE INDEX IF NOT EXISTS idx_handover_records_application ON animal_handover_records (application_id);
