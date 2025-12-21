-- Создаем базу данных, если ее нет
SELECT 'CREATE DATABASE accountant_db'
    WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'accountant_db')\gexec

-- Подключаемся к созданной базе
\c accountant_db;

-- Создаем пользователя, если его нет
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'accountant_user') THEN
        CREATE USER accountant_user WITH PASSWORD '6dx3AsIkyM';
END IF;
END
$$;

-- Даем все права пользователю на базу
GRANT ALL PRIVILEGES ON DATABASE accountant_db TO accountant_user;
GRANT CREATE ON DATABASE accountant_db TO accountant_user;

-- Создаем полезные расширения
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";